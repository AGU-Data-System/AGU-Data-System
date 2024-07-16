import { Either, failure, Failure, success } from '../../utils/Either';
import { Problem, problemMediaType } from '../../utils/Problem';

const API_ENDPOINT = 'http://192.168.40.242:8080/api';
/**
 * An error that occurs if there is a network error.
 */
export class NetworkError extends Error {
    constructor(message: any) {
        super(message);
    }
}

/**
 * An error that occurs if the response is not a Siren entity.
 */
export class UnexpectedResponseError extends Error {
    constructor(message: string) {
        super(message);
    }
}

async function fetchWithEither(url: string, options: RequestInit): Promise<Either<NetworkError, Response>> {
    try {
        const response = await fetch(url, options);
        return success(response);
    } catch (error) {
        if (error instanceof Error) {
            return failure(new NetworkError(error.message));
        } else {
            return failure(new NetworkError('Unknown network error'));
        }
    }
}

export async function fetchFunction(partialUrl: string, method: string, data: any = null, authentication: boolean = false, headers?: any): Promise<Either<Error | Problem, any>> {
    const url = API_ENDPOINT + partialUrl;
    const fetchResult = await fetchWithEither(url, {
        method: method,
        headers: {
            ...headers,
            'Content-Type': 'application/json'
        },
        body: data,
        credentials: authentication ? 'include' : 'omit'
    });

    if (fetchResult instanceof Failure) {
        return fetchResult;
    } else {
        const response = fetchResult.value;
        const contentType = response.headers.get('Content-Type');
        const body = await response.json();
        if (!response.ok) {
            if (contentType?.includes(problemMediaType)) {
                return failure(new Problem(body));
            } else {
                return failure(new UnexpectedResponseError(`Unexpected response type: ${contentType}`));
            }
        } else {
            return success(body);
        }
    }
}
