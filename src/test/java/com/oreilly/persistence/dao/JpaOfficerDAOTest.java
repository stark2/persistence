package com.oreilly.persistence.dao;

import com.oreilly.persistence.entities.Officer;
import com.oreilly.persistence.entities.Rank;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@SpringBootTest
@Transactional
public class JpaOfficerDAOTest {
    @Autowired
    private JpaOfficerDAO dao;

    @Autowired
    private JdbcTemplate template;

    // private method to retrieve the current ids in the database
    private List<Integer> getIds() {
        return template.query("select id from officers", (rs, num) -> rs.getInt("id"));
    }

    @Test
    public void testSave() throws Exception {
        Officer officer = new Officer(Rank.LIEUTENANT, "Nyota", "Uhuru");
        officer = dao.save(officer);
        Assertions.assertNotNull(officer.getId());
    }

    @Test
    public void findOneThatExists() throws Exception {
        getIds().forEach(id -> {
            Optional<Officer> officer = dao.findById(id);
            Assertions.assertTrue(officer.isPresent());
            Assertions.assertEquals(id, officer.get().getId());
        });
    }

    @Test
    public void findOneThatDoesNotExist() throws Exception {
        Optional<Officer> officer = dao.findById(999);
        Assertions.assertFalse(officer.isPresent());
    }

    @Test
    public void findAll() throws Exception {
        List<String> dbNames = dao.findAll().stream()
                .map(Officer::getLast)
                .collect(Collectors.toList());
        MatcherAssert.assertThat(dbNames, Matchers.containsInAnyOrder("Kirk", "Picard", "Sisko", "Janeway", "Archer"));
    }

    @Test
    public void count() throws Exception {
        Assertions.assertEquals(5, dao.count());
    }

    @Test
    public void delete() throws Exception {
        getIds().forEach(id -> {
            Optional<Officer> officer = dao.findById(id);
            Assertions.assertTrue(officer.isPresent());
            dao.delete(officer.get());
        });
        Assertions.assertEquals(0, dao.count());
    }

    @Test
    public void existsById() throws Exception {
        getIds().forEach(id -> Assertions.assertTrue(dao.existsById(id)));
    }
}
