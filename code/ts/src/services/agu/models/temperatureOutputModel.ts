interface TemperatureOutputModel {
    timestamp: string;
    predictionFor: string;
    min: number;
    max: number;
    [key: string]: string | number;  // Index signature to allow dynamic keys
}

export default TemperatureOutputModel;