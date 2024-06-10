package aguDataSystem.server.service.errors.transportCompany

sealed class GetTransportCompaniesOfAGUError {
    data object AGUNotFound : GetTransportCompaniesOfAGUError()
}