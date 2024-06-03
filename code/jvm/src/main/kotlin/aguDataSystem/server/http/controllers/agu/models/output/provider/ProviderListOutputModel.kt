package aguDataSystem.server.http.controllers.agu.models.output.provider

import aguDataSystem.server.domain.provider.Provider

/**
 * Output model for Provider
 *
 * @param providers The list of providers
 * @param size The size of the list
 */
data class ProviderListOutputModel(
	val providers: List<ProviderOutputModel>,
	val size: Int
) {
	constructor(providers: List<Provider>) : this(
		providers = providers.map { ProviderOutputModel(it) },
		size = providers.size
	)
}