interface GasOutputModel {
    timestamp: string;
    predictionFor: string;
    level: number;
    tankNumber: number;
    [key: string]: string | number;  // Index signature to allow dynamic keys
}

interface LevelsInputModel {
    min: number;
    max: number;
    critical: number;
}

interface GetGasListOutputModel {
    gasMeasures: GasOutputModel[];
    size: number;
}

export { GasOutputModel, LevelsInputModel, GetGasListOutputModel };