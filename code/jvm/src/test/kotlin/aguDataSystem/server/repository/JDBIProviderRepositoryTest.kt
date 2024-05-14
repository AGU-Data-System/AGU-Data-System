package aguDataSystem.server.repository

import aguDataSystem.server.domain.provider.GasProvider
import aguDataSystem.server.domain.provider.ProviderType
import aguDataSystem.server.repository.RepositoryUtils.DUMMY_DNO_NAME
import aguDataSystem.server.repository.RepositoryUtils.dummyAGU
import aguDataSystem.server.repository.agu.JDBIAGURepository
import aguDataSystem.server.repository.dno.JDBIDNORepository
import aguDataSystem.server.repository.provider.JDBIProviderRepository
import aguDataSystem.server.testUtils.SchemaManagementExtension
import aguDataSystem.server.testUtils.SchemaManagementExtension.testWithHandleAndRollback
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import org.jdbi.v3.core.statement.UnableToExecuteStatementException
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(SchemaManagementExtension::class)
class JDBIProviderRepositoryTest {

	@Test
	fun `add provider correctly`() = testWithHandleAndRollback { handle ->
		//arrange
		val providerRepository = JDBIProviderRepository(handle)
		val aguRepository = JDBIAGURepository(handle)
		val dnoRepository = JDBIDNORepository(handle)
		val agu = dummyAGU
		val dnoName = DUMMY_DNO_NAME
		val providerId = 1
		val providerType = ProviderType.GAS

		val dnoId = dnoRepository.addDNO(dnoName)
		aguRepository.addAGU(agu, dnoId)

		//act
		providerRepository.addProvider(agu.cui, providerId, providerType)

		val provider = providerRepository.getProviderByAGUAndType(agu.cui, providerType)

		//assert
		assertEquals(providerId, provider?.id)
		assert(provider?.measures?.isEmpty() ?: false)
		assert(provider is GasProvider)
	}

	@Test
	fun `create a provider without an AGU`() = testWithHandleAndRollback { handle ->
		//arrange
		val providerRepository = JDBIProviderRepository(handle)
		val providerId = 1
		val providerType = ProviderType.GAS

		//act & assert
		assertFailsWith<UnableToExecuteStatementException> {
			providerRepository.addProvider("nonExistentAGU", providerId, providerType)
		}
	}

	@Test
	fun `getProviderById correctly`() = testWithHandleAndRollback { handle ->
		//arrange
		val providerRepository = JDBIProviderRepository(handle)
		val aguRepository = JDBIAGURepository(handle)
		val dnoRepository = JDBIDNORepository(handle)
		val agu = dummyAGU
		val dnoName = DUMMY_DNO_NAME
		val providerId = 1
		val providerType = ProviderType.GAS

		val dnoId = dnoRepository.addDNO(dnoName)
		aguRepository.addAGU(agu, dnoId)
		providerRepository.addProvider(agu.cui, providerId, providerType)

		//act
		val provider = providerRepository.getProviderById(providerId)

		//assert
		assertEquals(providerId, provider?.id)
		assert(provider is GasProvider)
	}

	@Test
	fun `getProviderById with nonExistent id`() = testWithHandleAndRollback { handle ->
		//arrange
		val providerRepository = JDBIProviderRepository(handle)

		//act
		val provider = providerRepository.getProviderById(Int.MAX_VALUE)

		//assert
		assertNull(provider)
	}

	@Test
	fun `getProviderByAGUAndType correctly`() = testWithHandleAndRollback { handle ->
		//arrange
		val providerRepository = JDBIProviderRepository(handle)
		val aguRepository = JDBIAGURepository(handle)
		val dnoRepository = JDBIDNORepository(handle)
		val agu = dummyAGU
		val dnoName = DUMMY_DNO_NAME
		val providerId = 1
		val providerType = ProviderType.GAS

		val dnoId = dnoRepository.addDNO(dnoName)
		aguRepository.addAGU(agu, dnoId)
		providerRepository.addProvider(agu.cui, providerId, providerType)

		//act
		val provider = providerRepository.getProviderByAGUAndType(agu.cui, providerType)

		//assert
		assertEquals(providerId, provider?.id)
		assert(provider is GasProvider)
	}

	@Test
	fun `getProviderByAGUAndType with nonExistent AGU`() = testWithHandleAndRollback { handle ->
		//arrange
		val providerRepository = JDBIProviderRepository(handle)
		val providerType = ProviderType.GAS

		//act
		val provider = providerRepository.getProviderByAGUAndType("nonExistentAGU", providerType)

		//assert
		assertNull(provider)
	}

	@Test
	fun `getProviderByAGUAndType with wrong providerType`() = testWithHandleAndRollback { handle ->
		//arrange
		val providerRepository = JDBIProviderRepository(handle)
		val aguRepository = JDBIAGURepository(handle)
		val dnoRepository = JDBIDNORepository(handle)
		val agu = dummyAGU
		val dnoName = DUMMY_DNO_NAME
		val providerId = 1
		val providerType = ProviderType.GAS

		val dnoId = dnoRepository.addDNO(dnoName)
		aguRepository.addAGU(agu, dnoId)
		providerRepository.addProvider(agu.cui, providerId, providerType)

		//act
		val provider = providerRepository.getProviderByAGUAndType(agu.cui, ProviderType.TEMPERATURE)

		//assert
		assertNull(provider)
	}

//	@Test
//	fun `getAllProviders correctly`() = testWithHandleAndRollback { handle ->
//		//arrange
//		val providerRepository = JDBIProviderRepository(handle)
//		val aguRepository = JDBIAGURepository(handle)
//		val dnoRepository = JDBIDNORepository(handle)
//		val agu = dummyAGU
//		val dnoName = DUMMY_DNO_NAME
//
//		val dnoId = dnoRepository.addDNO(dnoName)
//		aguRepository.addAGU(agu, dnoId)
//
//		ProviderType.entries.forEachIndexed { index, it ->
//			providerRepository.addProvider(agu.cui, index+1, it)
//		}
//
//		//act
//		val providers = providerRepository.getAllProviders()
//
//		//assert
//		assertEquals(2, providers.size)
//		assert(providers.first() is GasProvider) // TODO Smelly code, stops working if the order of the enum changes
//		assert(providers.last() is TemperatureProvider)
//	}
//
//	@Test
//	fun `getAllProviders with no providers`() = testWithHandleAndRollback { handle ->
//		//arrange
//		val providerRepository = JDBIProviderRepository(handle)
//
//		//act
//		val providers = providerRepository.getAllProviders()
//
//		//assert
//		assert(providers.isEmpty())
//	}

	@Test
	fun `deleteProviderById correctly`() = testWithHandleAndRollback { handle ->
		//arrange
		val providerRepository = JDBIProviderRepository(handle)
		val aguRepository = JDBIAGURepository(handle)
		val dnoRepository = JDBIDNORepository(handle)
		val agu = dummyAGU
		val dnoName = DUMMY_DNO_NAME
		val providerId = 1
		val providerType = ProviderType.GAS

		val dnoId = dnoRepository.addDNO(dnoName)
		aguRepository.addAGU(agu, dnoId)
		providerRepository.addProvider(agu.cui, providerId, providerType)

		//act
		providerRepository.deleteProviderById(providerId, agu.cui)

		val provider = providerRepository.getProviderById(providerId)

		//assert
		assertNull(provider)
	}

	@Test
	fun `deleteProviderById with nonExistent provider`() = testWithHandleAndRollback { handle ->
		//arrange
		val providerRepository = JDBIProviderRepository(handle)

		//act
		providerRepository.deleteProviderById(Int.MAX_VALUE, "nonExistentAGU")
	}
}