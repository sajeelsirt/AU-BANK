package aubank.retail.liabilities.util;

import org.hibernate.dialect.PostgreSQL9Dialect;
import org.hibernate.type.descriptor.sql.LongVarcharTypeDescriptor;
import org.hibernate.type.descriptor.sql.SqlTypeDescriptor;
import org.springframework.stereotype.Component;

import java.sql.Types;

@Component
public class MidPointPostgreSQLDialect extends PostgreSQL9Dialect {


    @Override
    public SqlTypeDescriptor remapSqlTypeDescriptor(SqlTypeDescriptor sqlTypeDescriptor) {
        if (sqlTypeDescriptor.getSqlType() == Types.BLOB) {
            return LongVarcharTypeDescriptor.INSTANCE;
        }
        if (sqlTypeDescriptor.getSqlType() == Types.CLOB) {
            return LongVarcharTypeDescriptor.INSTANCE;
        }

/*        if (!sqlTypeDescriptor.canBeRemapped()) {
            return sqlTypeDescriptor;
        }*/
        return super.remapSqlTypeDescriptor(sqlTypeDescriptor);
    }

    public MidPointPostgreSQLDialect() {
        this.registerColumnType(Types.JAVA_OBJECT, "jsonb");
    }
}

