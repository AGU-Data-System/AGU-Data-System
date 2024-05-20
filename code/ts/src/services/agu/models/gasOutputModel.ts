interface GasOutputModel {
    timestamp: string;
    predictionFor: string;
    level: number;
    tankNumber: number;
    [key: string]: string | number;  // Index signature to allow dynamic keys
}

export default GasOutputModel;