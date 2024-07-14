package aguDataSystem.server.repository.jdbi.mappers.loads

import aguDataSystem.server.domain.load.ScheduledLoad
import aguDataSystem.server.domain.load.TimeOfDay
import java.sql.ResultSet
import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext

/**
 * Maps the row of the result set to a [ScheduledLoad]
 * @see RowMapper
 * @see ScheduledLoad
 */
class ScheduledLoadMapper : RowMapper<ScheduledLoad> {

        /**
        * Maps a row from the database to a [ScheduledLoad]
        *
        * @param rs the result set
        * @param ctx the statement context
        * @return the [ScheduledLoad] from the result set
        */
        override fun map(rs: ResultSet, ctx: StatementContext?): ScheduledLoad {
            return ScheduledLoad(
                id = rs.getInt("id"),
                aguCui = rs.getString("agu_cui"),
                date = rs.getDate("local_date").toLocalDate(),
                timeOfDay = TimeOfDay.valueOf(rs.getString("time_of_day").uppercase()),
                amount = rs.getDouble("amount"),
                isManual = rs.getBoolean("is_manual"),
                isConfirmed = rs.getBoolean("is_confirmed")
            )
        }
}