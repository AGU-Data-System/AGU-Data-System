package aguDataSystem.server.repository.jdbi.mappers

import aguDataSystem.server.domain.company.DNO
import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import java.sql.ResultSet

class DNOMapper : RowMapper<DNO> {
    override fun map(rs: ResultSet, ctx: StatementContext?): DNO {
        return DNO(
            rs.getInt("id"),
            rs.getString("name")
        )
    }
}