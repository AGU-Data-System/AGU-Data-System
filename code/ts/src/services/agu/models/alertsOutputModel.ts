interface AlertsOutputModel {
    id: number;
    agu: string;
    timestamp: string;
    title: string;
    message: string;
    isResolved: boolean;
}

interface ListAlertsOutputModel {
    alerts: AlertsOutputModel[];
    size: number;
}

export { AlertsOutputModel, ListAlertsOutputModel }