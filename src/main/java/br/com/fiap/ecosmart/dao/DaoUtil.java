package br.com.fiap.ecosmart.dao;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

// Pequenos auxiliares compartilhados pelos DAOs
final class DaoUtil {

    private DaoUtil() {}

    // Lê uma coluna DATE do Oracle como LocalDate (aceita null)
    static LocalDate data(ResultSet rs, String coluna) throws SQLException {
        Date d = rs.getDate(coluna);
        return d == null ? null : d.toLocalDate();
    }

    // Converte LocalDate em java.sql.Date para gravar no Oracle (aceita null)
    static Date sql(LocalDate data) {
        return data == null ? null : Date.valueOf(data);
    }
}
