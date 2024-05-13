interface AguCreateInputModel {
    cui: string;
    name: string;
    minLevel: number;
    maxLevel: number;
    criticalLevel: number;
    loadVolume: number;
    latitude: number;
    longitude: number;
    locationName: string;
    dnoName: string;
    gasLevelUrl: string;
    image: string;
    tanks: TankCreateInputModel[];
    contacts: ContactCreateInputModel[];
    isFavorite: boolean;
    notes: string;
}

interface AguCreateWithoutTanksAndContactsModel {
    cui: string;
    name: string;
    minLevel: string;
    maxLevel: string;
    criticalLevel: string;
    loadVolume: string;
    latitude: string;
    longitude: string;
    locationName: string;
    dnoName: string;
    gasLevelUrl: string;
    image: string;
    isFavorite: boolean;
    notes: string;
}

interface TankCreateInputModel {
    number: number;
    minLevel: number;
    maxLevel: number;
    criticalLevel: number;
    loadVolume: number;
    capacity: number;
}

interface ContactCreateInputModel {
    name: string;
    phone: string;
    type: string;
}

export { AguCreateInputModel, AguCreateWithoutTanksAndContactsModel, TankCreateInputModel, ContactCreateInputModel };