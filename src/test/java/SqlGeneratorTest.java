import com.kjsoft.sorm.infrastructure.persistence.datafilter.QueryDslSqlGenerator;
import com.kjsoft.sorm.infrastructure.persistence.datafilter.basefilter.PersonBaseFilter;
import com.kjsoft.sorm.infrastructure.persistence.datafilter.basefilter.condition.StringTermExactCondition;
import com.kjsoft.sorm.infrastructure.persistence.datafilter.entityfilter.user.UserPersistenceFilter;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


public class SqlGeneratorTest {

    @Test
    public void userFilterByFirstNameEquals() {
        var generator = new QueryDslSqlGenerator();

        var cursor = "23";
        var limit = 10;

        var cursorLong = 23L;
        var limitLong = 10L;
        var firstNameValue = "Ivan";

        var fullName = new StringTermExactCondition(null, null, firstNameValue, null);
        var personBaseFilter = new PersonBaseFilter(null, fullName, null, null, null, null);

        var userFilter = new UserPersistenceFilter();
        userFilter.setName(personBaseFilter);

        var result = generator.generateSelect(userFilter, cursor, null, limit);

        var expectedParams = new LinkedList<>();
        expectedParams.add(firstNameValue);
        expectedParams.add(cursorLong);
        expectedParams.add(limitLong);

        var expectedSql = """
                select u.id, u.full_name, u.first_name, u.last_name, u.email, u.date_created
                from users u
                where u.first_name = ? and u.id > ?
                order by u.id asc
                limit ?""";

        assertArrayEquals(expectedParams.toArray(), result.getSecond().toArray());
        assertEquals(expectedSql, result.getFirst());
    }

    @Test
    public void userFilterByFirstNameEqualsWithAnd() {
        var generator = new QueryDslSqlGenerator();

        var cursor = "23";
        var limit = 10;

        var cursorLong = 23L;
        var limitLong = 10L;
        var firstNameValueMain = "Ivan";
        var firstNameValueAnd = "Piter";


        var userFilterMain = new UserPersistenceFilter();
        var fullNameMain = new StringTermExactCondition(null, null, firstNameValueMain, null);
        userFilterMain.setName(new PersonBaseFilter(null, fullNameMain, null, null, null, null));

        //AND----------------------------
        var userFilterAnd = new UserPersistenceFilter();
        var fullNameAnd = new StringTermExactCondition(null, null, firstNameValueAnd, null);
        userFilterAnd.setName(new PersonBaseFilter(null, fullNameAnd, null, null, null, null));
        //-------------------------------

        userFilterMain.setAnd(List.of(userFilterAnd));


        var result = generator.generateSelect(userFilterMain, cursor, null, limit);

        var expectedParams = new LinkedList<>();
        expectedParams.add(firstNameValueMain);
        expectedParams.add(firstNameValueAnd);
        expectedParams.add(cursorLong);
        expectedParams.add(limitLong);

        var expectedSql = """
                select u.id, u.full_name, u.first_name, u.last_name, u.email, u.date_created
                from users u
                where u.first_name = ? and u.first_name = ? and u.id > ?
                order by u.id asc
                limit ?""";

        assertArrayEquals(expectedParams.toArray(), result.getSecond().toArray());
        assertEquals(expectedSql, result.getFirst());
    }

    @Test
    public void userFilterByFirstNameEqualsWithMultipleOr() {
        var generator = new QueryDslSqlGenerator();

        var cursor = "23";
        var limit = 10;

        var cursorLong = 23L;
        var limitLong = 10L;


        var userFilterMain = new UserPersistenceFilter();
        var firstNameValueMain = "Ivan";
        var firstNameMain = new StringTermExactCondition(null, null, firstNameValueMain, null);
        userFilterMain.setName(new PersonBaseFilter(null, firstNameMain, null, null, null, null));

        //OR----------------------------------------
        var userFilterOr1 = new UserPersistenceFilter();
        var firstNameValueOr1 = "Piter";
        var firstNameOr1 = new StringTermExactCondition(null, null, firstNameValueOr1, null);
        userFilterOr1.setName(new PersonBaseFilter(null, firstNameOr1, null, null, null, null));

        var userFilterOr2 = new UserPersistenceFilter();
        var firstNameValueOr2 = "Arcadi";
        var firstNameOr2 = new StringTermExactCondition(null, null, firstNameValueOr2, null);
        userFilterOr2.setName(new PersonBaseFilter(null, firstNameOr2, null, null, null, null));
        //-------------------------------------------

        userFilterMain.setOr(List.of(userFilterOr1, userFilterOr2));


        var result = generator.generateSelect(userFilterMain, cursor, null, limit);

        var expectedParams = new LinkedList<>();
        expectedParams.add(firstNameValueMain);
        expectedParams.add(firstNameValueOr1);
        expectedParams.add(firstNameValueOr2);
        expectedParams.add(cursorLong);
        expectedParams.add(limitLong);

        var expectedSql = """
                select u.id, u.full_name, u.first_name, u.last_name, u.email, u.date_created
                from users u
                where u.first_name = ? and (u.first_name = ? or u.first_name = ?) and u.id > ?
                order by u.id asc
                limit ?""";

        assertArrayEquals(expectedParams.toArray(), result.getSecond().toArray());
        assertEquals(expectedSql, result.getFirst());
    }

    @Test
    public void userFilterByFirstNameEqualsWithMultipleOrWithAnd() {
        var generator = new QueryDslSqlGenerator();

        var cursor = "23";
        var limit = 10;

        var cursorLong = 23L;
        var limitLong = 10L;


        var userFilterMain = new UserPersistenceFilter();
        var firstNameValueMain = "Ivan";
        var firstNameMain = new StringTermExactCondition(null, null, firstNameValueMain, null);
        userFilterMain.setName(new PersonBaseFilter(null, firstNameMain, null, null, null, null));

        //OR1----------------------------------------
        var userFilterOr1 = new UserPersistenceFilter();
        var firstNameValueOr1 = "Piter";
        var firstNameOr1 = new StringTermExactCondition(null, null, firstNameValueOr1, null);
        userFilterOr1.setName(new PersonBaseFilter(null, firstNameOr1, null, null, null, null));

        //---AND
        var userFilterOr1And = new UserPersistenceFilter();
        var firstNameValueOr1And = "Nick";
        var firstNameOr1And = new StringTermExactCondition(null, null, firstNameValueOr1And, null);
        userFilterOr1And.setName(new PersonBaseFilter(null, firstNameOr1And, null, null, null, null));
        //------

        userFilterOr1.setAnd(List.of(userFilterOr1And));
        //OR1_END----------------------------------------

        //OR2----------------------------------------
        var userFilterOr2 = new UserPersistenceFilter();
        var firstNameValueOr2 = "Arcadi";
        var firstNameOr2 = new StringTermExactCondition(null, null, firstNameValueOr2, null);
        userFilterOr2.setName(new PersonBaseFilter(null, firstNameOr2, null, null, null, null));
        //OR2_END----------------------------------------

        userFilterMain.setOr(List.of(userFilterOr1, userFilterOr2));

        var result = generator.generateSelect(userFilterMain, cursor, null, limit);

        var expectedParams = new LinkedList<>();
        expectedParams.add(firstNameValueMain);
        expectedParams.add(firstNameValueOr1);
        expectedParams.add(firstNameValueOr1And);
        expectedParams.add(firstNameValueOr2);
        expectedParams.add(cursorLong);
        expectedParams.add(limitLong);

        var expectedSql = """
                select u.id, u.full_name, u.first_name, u.last_name, u.email, u.date_created
                from users u
                where u.first_name = ? and (u.first_name = ? and u.first_name = ? or u.first_name = ?) and u.id > ?
                order by u.id asc
                limit ?""";

        assertArrayEquals(expectedParams.toArray(), result.getSecond().toArray());
        assertEquals(expectedSql, result.getFirst());
    }

    @Test
    public void userFilterByFirstNameEqualsAndWithNot() {
        var generator = new QueryDslSqlGenerator();

        var cursor = "23";
        var limit = 10;

        var cursorLong = 23L;
        var limitLong = 10L;


        var userFilterMain = new UserPersistenceFilter();
        var firstNameValueMain = "Ivan";
        var firstNameMain = new StringTermExactCondition(null, null, firstNameValueMain, null);
        userFilterMain.setName(new PersonBaseFilter(null, firstNameMain, null, null, null, null));

        var userFilterNot = new UserPersistenceFilter();
        var firstNameValueNot = "Svetlana";
        var firstNameNot = new StringTermExactCondition(null, null, firstNameValueNot, null);
        userFilterNot.setName(new PersonBaseFilter(null, firstNameNot, null, null, null, null));


        userFilterMain.setNot(userFilterNot);

        var result = generator.generateSelect(userFilterMain, cursor, null, limit);

        var expectedParams = new LinkedList<>();
        expectedParams.add(firstNameValueMain);
        expectedParams.add(firstNameValueNot);
        expectedParams.add(cursorLong);
        expectedParams.add(limitLong);

        var expectedSql = """
                select u.id, u.full_name, u.first_name, u.last_name, u.email, u.date_created
                from users u
                where u.first_name = ? and not u.first_name = ? and u.id > ?
                order by u.id asc
                limit ?""";

        assertArrayEquals(expectedParams.toArray(), result.getSecond().toArray());
        assertEquals(expectedSql, result.getFirst());
    }

    @Test
    public void userFilterByFirstNameEqualsNot() {
        var generator = new QueryDslSqlGenerator();

        var cursor = "23";
        var limit = 10;

        var cursorLong = 23L;
        var limitLong = 10L;


        var userFilterMain = new UserPersistenceFilter();

        var userFilterOrNot = new UserPersistenceFilter();
        var firstNameValueOrNot = "Svetlana";
        var firstNameOrNot = new StringTermExactCondition(null, null, firstNameValueOrNot, null);
        userFilterOrNot.setName(new PersonBaseFilter(null, firstNameOrNot, null, null, null, null));


       userFilterMain.setNot(userFilterOrNot);

        var result = generator.generateSelect(userFilterMain, cursor, null, limit);

        var expectedParams = new LinkedList<>();
        expectedParams.add(firstNameValueOrNot);
        expectedParams.add(cursorLong);
        expectedParams.add(limitLong);

        var expectedSql = """
                select u.id, u.full_name, u.first_name, u.last_name, u.email, u.date_created
                from users u
                where not u.first_name = ? and u.id > ?
                order by u.id asc
                limit ?""";

        assertArrayEquals(expectedParams.toArray(), result.getSecond().toArray());
        assertEquals(expectedSql, result.getFirst());
    }
}