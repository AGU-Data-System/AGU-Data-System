import { fetchFunction } from "../utils/fetchFunction";
import { Either } from "../../utils/Either";
import {AguDetailsOutputModel, AguOutputModel, AgusBasicInfoOutputModel} from "./models/aguOutputModel";
import { AguCreateInputModel } from "./models/createAguInputModel";

export namespace aguService {
    export async function getAGUs(): Promise<Either<Error, AgusBasicInfoOutputModel[]>> {
        const url = `/agus`;
        return fetchFunction(url, "GET", null);
    }

    export async function getAguById(aguCui: string): Promise<Either<Error, AguDetailsOutputModel>> {
        const url = `/agus/${aguCui}`;
        return fetchFunction(url, "GET", null);
    }

    export async function createAgu(aguData: AguCreateInputModel): Promise<Either<Error, AguOutputModel>> {
        const url = `/agus/create`;
        return fetchFunction(url, "POST", JSON.stringify(aguData));
    }
}