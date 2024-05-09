package aguDataSystem.server.domain.agu

import aguDataSystem.server.domain.Location
import aguDataSystem.server.domain.company.DNO

/**
 * Represents the basic information of the Autonomous Gas Unit (AGU) in the system.
 *
 * @property cui CUI of the AGU
 * @property name name of the AGU
 * @property dno DNO of the AGU
 * @property location location of the AGU
 */
data class AGUBasicInfo(
    val cui: String,
    val name: String,
    val dno: DNO,
    val location: Location
)