package aguDataSystem.server.repository.dno

import aguDataSystem.server.domain.company.DNO

interface DNORepository {

    fun addDNO(name: String)

    fun getByName(name: String): DNO?

    fun getById(id: Int): DNO?

    fun isDNOStored(name: String): Boolean
}