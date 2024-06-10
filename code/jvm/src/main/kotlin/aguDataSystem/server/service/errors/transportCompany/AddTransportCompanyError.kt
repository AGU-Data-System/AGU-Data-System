package aguDataSystem.server.service.errors.transportCompany

sealed class AddTransportCompanyError {
    data object InvalidName : AddTransportCompanyError()
    data object TransportCompanyAlreadyExists : AddTransportCompanyError()
}