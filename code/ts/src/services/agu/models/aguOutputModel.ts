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
        region: string;
    };
    image: string;
    contacts: ContactListOutputModel;
    tanks: TanksListOutputModel;
    providers: any[];
    isFavorite: boolean;
    notes: string;
    training: any;
    capacity: number;
    correctionFactor: number;
}

interface ContactOutputModel {
    id: number;
    name: string;
    phone: string;
    type: string;
}

interface ContactInputModel {
    name: string;
    phone: string;
    type: string;
}

interface TankOutputModel {
    number: number;
    levels: {
        min: number;
        max: number;
        critical: number;
    };
    loadVolume: number;
    capacity: number;
    correctionFactor: number;
}

interface TankInputModel {
    number: number,
    minLevel: number,
    maxLevel: number,
    criticalLevel: number,
    loadVolume: number,
    capacity: number
    correctionFactor: number
}

interface TankAddOutputModel {
    number: number;
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

interface AgusBasicInfoListOutputModel {
    agusBasicInfo: AgusBasicInfoOutputModel[];
    size: number;
}

interface ContactListOutputModel {
    contacts: ContactOutputModel[];
    size: number;
}

interface TanksListOutputModel {
    tanks: TankOutputModel[];
    size: number;

}

export { AguDetailsOutputModel, ContactOutputModel, ContactInputModel, TankOutputModel, TankAddOutputModel, TankInputModel, AguOutputModel, AgusBasicInfoOutputModel, AgusBasicInfoListOutputModel };