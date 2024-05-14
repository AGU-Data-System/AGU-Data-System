import { fetchFunction } from "../utils/fetchFunction";
import { Either } from "../../utils/Either";
import {AguDetailsOutputModel, AguOutputModel, AgusBasicInfoOutputModel} from "./models/aguOutputModel";
import { AguCreateInputModel } from "./models/createAguInputModel";
import {Problem} from "../../utils/Problem";

export namespace aguService {
    export async function getAGUs(): Promise<Either<Error | Problem, AgusBasicInfoOutputModel[]>> {
        const url = `/agus`;
        return fetchFunction(url, "GET", null);
    }

    export async function getAguById(aguCui: string): Promise<Either<Error | Problem, AguDetailsOutputModel>> {
        const url = `/agus/${aguCui}`;
        return fetchFunction(url, "GET", null);
    }

    export async function createAgu(aguData: AguCreateInputModel): Promise<Either<Error | Problem, AguOutputModel>> {
        const url = `/agus/create`;
        return fetchFunction(url, "POST", JSON.stringify(aguData));
    }
}