import { fetchFunction } from "../utils/fetchFunction";
import { Either } from "../../utils/Either";
import {
    AguDetailsOutputModel,
    AguOutputModel,
    AgusBasicInfoListOutputModel,
    ContactInputModel, TankAddOutputModel,
    TankInputModel
} from "./models/aguOutputModel";
import { AguCreateInputModel } from "./models/createAguInputModel";
import { Problem } from "../../utils/Problem";
import { GetTemperatureListOutputModel } from "./models/temperatureOutputModel";
import { GetGasListOutputModel, LevelsInputModel } from "./models/gasOutputModel";

export namespace aguService {
    export async function getAGUs(): Promise<Either<Error | Problem, AgusBasicInfoListOutputModel>> {
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

    export async function updateFavouriteOnAGU(aguCui: string, isFavourite: boolean): Promise<Either<Error | Problem, void>> {
        const url = `/agus/${aguCui}/favourite`;
        return fetchFunction(url, "PUT", JSON.stringify({isFavourite}));
    }

    export async function getTemperatureData(aguCui: string): Promise<Either<Error | Problem, GetTemperatureListOutputModel>> {
        const url = `/agus/${aguCui}/temperature`;
        return fetchFunction(url, "GET");
    }

    export async function updateAguNotes(aguCui: string, notes: string): Promise<Either<Error | Problem, void>> {
        const url = `/agus/${aguCui}/notes`;
        return fetchFunction(url, "PUT", JSON.stringify({notes}));
    }

    export async function getGasData(aguCui: string): Promise<Either<Error | Problem, GetGasListOutputModel>> {
        const url = `/agus/${aguCui}/gas/daily`;
        return fetchFunction(url, "GET");
    }

    export async function getHourlyGasData(aguCui: string, day: number, month: number, year: number): Promise<Either<Error | Problem, GetGasListOutputModel>> {
        const url = `/agus/${aguCui}/gas/hourly?day=${year}-${month.toString().padStart(2, '0')}-${day.toString().padStart(2, '0')}`;
        return fetchFunction(url, "GET");
    }

    export async function updateAguActive(aguCui: string, isActive: boolean): Promise<Either<Error | Problem, AguDetailsOutputModel>> {
        const url = `/agus/${aguCui}/active`;
        return fetchFunction(url, "PUT", JSON.stringify({isActive}));
    }

    export async function addContact(aguCui: string, newContact: ContactInputModel): Promise<Either<Error | Problem, number>> {
        const url = `/agus//${aguCui}/contact`;
        return fetchFunction(url, "POST", JSON.stringify(newContact));
    }

    export async function deleteContact(aguCui: string, contactId: number): Promise<Either<Error | Problem, void>> {
        const url = `/agus//${aguCui}/contact/${contactId}`;
        return fetchFunction(url, "DELETE");
    }

    export async function addTank(aguCui: string, newTank: TankInputModel): Promise<Either<Error | Problem, TankAddOutputModel>> {
        const url = `/agus//${aguCui}/tank`;
        return fetchFunction(url, "POST", JSON.stringify(newTank));
    }

    export async function updateAguLevels(aguCui: string, levels: LevelsInputModel): Promise<Either<Error | Problem, AguDetailsOutputModel>> {
        const url = `/agus/${aguCui}/levels`;
        return fetchFunction(url, "PUT", JSON.stringify(levels));
    }

    export async function updateTank(aguCui: string, tankNumber: number, tankData: TankInputModel): Promise<Either<Error | Problem, AguDetailsOutputModel>> {
        const url = `/agus//${aguCui}/tank/${tankNumber}`;
        return fetchFunction(url, "PUT", JSON.stringify(tankData));
    }
}