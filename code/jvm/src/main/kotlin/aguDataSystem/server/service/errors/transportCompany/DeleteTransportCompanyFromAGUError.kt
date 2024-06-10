package aguDataSystem.server.service.errors.transportCompany

sealed class DeleteTransportCompanyFromAGUError {

    data object TransportCompanyNotFound : DeleteTransportCompanyFromAGUError()
    data object AGUNotFound : DeleteTransportCompanyFromAGUError()
}