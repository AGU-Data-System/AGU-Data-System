interface WeeklyPlanListOutputModel {
    startWeekDay: string;
    endWeekDay: string;
    loads: PlannedLoadOutputModel[];
    size: number;
}

interface PlannedLoadOutputModel {
    loadId: number;
    aguCui: string;
    locationName: string;
    date: string;
    timeOfDay: string;
    amount: string;
    isManual: string;
    isConfirmed: string;
}

interface CreatePlannedLoadInputModel {
    aguCui: string;
    date: string;
    timeOfDay: string;
    amount: string;
    isManual: string;
}

interface CreatePlannedLoadOutputModel {
    id: number;
}

interface BooleanLoadOutputModel {
    value: boolean;
}

export { WeeklyPlanListOutputModel, PlannedLoadOutputModel, CreatePlannedLoadInputModel, CreatePlannedLoadOutputModel, BooleanLoadOutputModel }