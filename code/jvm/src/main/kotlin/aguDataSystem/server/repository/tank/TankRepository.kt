package aguDataSystem.server.repository.tank

import aguDataSystem.server.domain.Tank

/**
 * A repository for the tanks of an AGU
 */
interface TankRepository {

	/**
	 * Adds a tank to an AGU
	 *
	 * @param cui CUI of the AGU
	 * @param tank Tank to add
	 * @return the number of the tank
	 */
	fun addTank(cui: String, tank: Tank): Int

	/**
	 * Gets all the tanks of an AGU
	 *
	 * @param cui the cui of the AGU
	 * @return a list of all the tanks of the AGU
	 */
	fun getAGUTanks(cui: String): List<Tank>

	/**
	 * Gets the tank by its number
	 *
	 * @param cui the cui of the AGU
	 * @param number the number of the tank
	 */
	fun getTankByNumber(cui: String, number: Int): Tank?

	/**
	 * Deletes a tank from an AGU
	 *
	 * @param cui the cui of the AGU
	 * @param number the number of the tank
	 */
	fun deleteTank(cui: String, number: Int)

	/**
	 * Updates a tank from an AGU
	 *
	 * @param cui the cui of the AGU
	 * @param tank the tank to update
	 */
	fun updateTank(cui: String, tank: Tank)

	/**
	 * Checks the number of tanks in an AGU
	 *
	 * @param cui the cui of the AGU
	 * @return the number of tanks in the AGU
	 */
	fun getNumberOfTanks(cui: String): Int
}
