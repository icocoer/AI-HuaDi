package com.example.work_program.service;

import com.example.work_program.entity.SysDict;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class SysDictServiceTest {

    @Autowired
    private SysDictService sysDictService;

    @Test
    void testFindByType() {
        List<SysDict> dicts = sysDictService.findByType("risk_level");
        assertEquals(3, dicts.size());
    }

    @Test
    void testFindAllTypes() {
        List<String> types = sysDictService.findAllTypes();
        assertTrue(types.contains("risk_level"));
    }

    @Test
    void testAddAndFind() {
        SysDict dict = new SysDict();
        dict.setDictType("test_type");
        dict.setDictKey("key1");
        dict.setDictValue("值1");
        dict.setSort(1);
        sysDictService.add(dict);

        assertNotNull(dict.getId());
        SysDict found = sysDictService.findById(dict.getId());
        assertEquals("值1", found.getDictValue());
    }

    @Test
    void testUpdate() {
        List<SysDict> dicts = sysDictService.findByType("risk_level");
        SysDict dict = dicts.get(0);
        dict.setDictValue("低风险(已修改)");
        sysDictService.update(dict);

        SysDict updated = sysDictService.findById(dict.getId());
        assertEquals("低风险(已修改)", updated.getDictValue());
    }

    @Test
    void testDelete() {
        SysDict dict = new SysDict();
        dict.setDictType("temp");
        dict.setDictKey("tmp");
        dict.setDictValue("临时");
        dict.setSort(0);
        sysDictService.add(dict);

        sysDictService.deleteById(dict.getId());
        assertNull(sysDictService.findById(dict.getId()));
    }
}
