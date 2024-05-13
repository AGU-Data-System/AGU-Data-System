interface AguDetailsOutputModel {
    cui: string;
    name: string;
    levels: {
        min: number;
        max: number;
        critical: number;
    };
    loadVolume: number;
    location: {
        name: string;
        latitude: number;
        longitude: number;
    };
    dno: {
        id: number;
        name: string;
    };
    image: string;
    contacts: ContactOutputModel[];
    tanks: TankOutputModel[];
    providers: any[];
    isFavorite: boolean;
    notes: string;
    training: any;
    capacity: number;
    correctionFactor: number;
}

interface ContactOutputModel {
    name: string;
    phone: string;
    type: string;
}

interface TankOutputModel {
    number: number;
    minLevel: number;
    maxLevel: number;
    criticalLevel: number;
    loadVolume: number;
    capacity: number;
}

interface AguOutputModel {
    cui: string;
}

interface AgusBasicInfoOutputModel {
    cui: string;
    name: string;
    dno: {
        id: number;
        name: string;
    };
    location: {
        name: string;
        latitude: number;
        longitude: number;
    };
}

export { AguDetailsOutputModel, ContactOutputModel, TankOutputModel, AguOutputModel, AgusBasicInfoOutputModel };