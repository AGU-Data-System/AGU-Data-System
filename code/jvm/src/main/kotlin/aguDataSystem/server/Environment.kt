package aguDataSystem.server

/**
 * Environment variables
 */
object Environment {

	/**
	 * Get the database url
	 */
	fun getDbUrl() = System.getenv(KEY_DB_URL) ?: throw Exception("Missing env var $KEY_DB_URL")

	/**
	 * Get the fetcher url
	 */
	fun getFetcherUrl() = System.getenv(KEY_FETCHER_URL) ?: throw Exception("Missing env var $KEY_FETCHER_URL")

	private const val KEY_DB_URL = "DB_URL"
	private const val KEY_FETCHER_URL = "FETCHER_URL"
}