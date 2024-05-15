import { fetchFunction } from "../utils/fetchFunction";
import { Either } from "../../utils/Either";
import {AguDetailsOutputModel, AguOutputModel, AgusBasicInfoOutputModel} from "./models/aguOutputModel";
import { AguCreateInputModel } from "./models/createAguInputModel";
import {Problem} from "../../utils/Problem";

export namespace aguService {
    export async function getAGUs(): Promise<Either<Error | Problem, AgusBasicInfoOutputModel[]>> {
        const url = `/agus`;
        return fetchFunction(url, "GET");
    }

    export async function getAguById(aguCui: string): Promise<Either<Error | Problem, AguDetailsOutputModel>> {
        const url = `/agus/${aguCui}`;
        return fetchFunction(url, "GET");
    }

    export async function createAgu(aguData: AguCreateInputModel): Promise<Either<Error | Problem, AguOutputModel>> {
        const url = `/agus/create`;
        return fetchFunction(url, "POST", JSON.stringify(aguData));
    }

    export async function getFavouriteAgus(): Promise<Either<Error | Problem, AgusBasicInfoOutputModel[]>> {
        const url = `/agus/favourites`;
        return fetchFunction(url, "GET");
    }

    export async function updateFavouriteOnAGU(aguCui: string, isFavourite: boolean): Promise<Either<Error | Problem, void>> {
        const url = `/agus/${aguCui}/favourite`;
        return fetchFunction(url, "PUT", JSON.stringify({isFavourite}));
    }
}