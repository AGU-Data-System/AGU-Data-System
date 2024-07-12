package aguDataSystem.server.repository.jdbi.mappers

import aguDataSystem.server.domain.alerts.Alert
import org.jdbi.v3.core.mapper.RowMapper
import java.sql.ResultSet
import org.jdbi.v3.core.statement.StatementContext

class AlertsMapper : RowMapper<Alert> {
    /**
     * Maps the row of the result set to an [Alert]
     *
     * @param rs the result set
     * @param ctx the statement context
     * @return the [Alert] from the result set
     */
    override fun map(rs: ResultSet, ctx: StatementContext?): Alert {
        return Alert(
            id = rs.getInt("id"),
            agu = rs.getString("agu_cui"),
            timestamp = rs.getTimestamp("timestamp").toInstant(),
            title = rs.getString("title"),
            message = rs.getString("message"),
            isResolved = rs.getBoolean("is_resolved")
        )
    }
}