export class Failure<L> {
    constructor(public value: L) {}
}

export class Success<R> {
    constructor(public value: R) {}
}

export type Either<L, R> = Failure<L> | Success<R>;

export function success<R>(value: R): Either<never, R> {
    return new Success<R>(value);
}

export function failure<L>(error: L): Either<L, never> {
    return new Failure<L>(error);
}