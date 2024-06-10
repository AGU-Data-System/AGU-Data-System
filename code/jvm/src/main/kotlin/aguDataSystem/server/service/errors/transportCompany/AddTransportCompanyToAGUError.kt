package aguDataSystem.server.service.errors.transportCompany

sealed class AddTransportCompanyToAGUError {
    data object AGUNotFound : AddTransportCompanyToAGUError()
    data object TransportCompanyNotFound : AddTransportCompanyToAGUError()
}