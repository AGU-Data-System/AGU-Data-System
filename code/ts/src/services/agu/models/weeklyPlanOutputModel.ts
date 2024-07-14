interface WeeklyPlanListOutputModel {
    startWeekDay: string;
    endWeekDay: string;
    loads: PlannedLoadOutputModel[];
    size: number;
}

interface PlannedLoadOutputModel {
    aguCui: string;
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

export { WeeklyPlanListOutputModel, PlannedLoadOutputModel, CreatePlannedLoadInputModel, CreatePlannedLoadOutputModel }