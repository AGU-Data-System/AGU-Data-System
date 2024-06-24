interface WeeklyPlanOutputModel {
    weekStartDay: string;
    weekEndDay: string;
    plannedAgus: AguPlannedOutputModel[];
}

interface AguPlannedOutputModel {
    aguCui: number;
    aguName: string;
    dayOfThePlaning: number;
    currentGasLevel: number;
    plannedGasLevel: number;
}

export { WeeklyPlanOutputModel }