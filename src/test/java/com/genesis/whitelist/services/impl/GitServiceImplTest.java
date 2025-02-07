package com.genesis.whitelist.services.impl;

import com.genesis.whitelist.exceptions.OperatorAlreadyExistsException;
import com.genesis.whitelist.exceptions.OperatorMissingException;
import com.genesis.whitelist.model.AddIpsRequest;
import com.genesis.whitelist.model.Operator;
import com.genesis.whitelist.utils.GitClient;
import io.quarkus.test.InjectMock;
import io.quarkus.test.Mock;
import io.quarkus.test.component.QuarkusComponentTest;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.when;

@QuarkusComponentTest
class GitServiceImplTest {

    @InjectMock
    GitClient gitClient;

    @Inject
    GitServiceImpl gitService;


    @BeforeEach
    void setup(@TempDir File testDir) throws IOException {
        File customersDir = new File(testDir, "customers");
        customersDir.mkdir();
        Files.write(customersDir.toPath().resolve("TestPartner1.conf"), "allow    192.168.0.1;\n".getBytes(),StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        Files.write(customersDir.toPath().resolve("TestPartner1.conf"), "allow    192.168.0.2;\n".getBytes(), StandardOpenOption.APPEND);
        Files.write(customersDir.toPath().resolve("TestPartner2.conf"), "allow    192.168.0.4;\n".getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        Files.write(customersDir.toPath().resolve("TestPartner3.conf"), "allow    192.168.0.5;\n".getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        when(gitClient.getRepoPath()).thenReturn(testDir);
    }


    @Test
    void getOperatorIPsSuccessfully() {
        var ips = gitService.getOperatorIPs("TestPartner1");
        assertEquals(2, ips.size());
        assertTrue(ips.contains("192.168.0.1"));
        assertTrue(ips.contains("192.168.0.2"));
    }
    @Test
    void getOperatorIPsThrowsOperatorDoesNotExist() {
        assertThrows(OperatorMissingException.class, () -> gitService.getOperatorIPs("NonExistingOperator"));
    }


    @Test
    void getAllOperators() {
        var allOperators = gitService.getAllOperators();
        assertEquals(3, allOperators.size());
        assertTrue(allOperators.contains(new Operator("TestPartner1")));
        assertTrue(allOperators.contains(new Operator("TestPartner2")));
        assertTrue(allOperators.contains(new Operator("TestPartner3")));
    }


    @Test
    void addNewIPsToExistingOperatorSuccessfully(){
        AddIpsRequest request = new AddIpsRequest();
        request.setNewIps((List.of("111.111.111.111", "222.222.222.222",  "222.222.222.222"))); // with a duplicate
        request.setWhitelistType(AddIpsRequest.WhitelistTypeEnum.API);
        gitService.addNewIPs("TestPartner1", request);
        var updatedIPList = gitService.getOperatorIPs("TestPartner1");
        assertEquals(4, updatedIPList.size());
        assertTrue(updatedIPList.contains("111.111.111.111"));
        assertTrue(updatedIPList.contains("222.222.222.222"));
    }
    @Test
    void addNewIPsThrowsOperatorDoesNotExist() {
        assertThrows(OperatorMissingException.class, () -> gitService.addNewIPs("NonExistingPartner", new AddIpsRequest()));
    }


    @Test
    void addNewOperatorThrowsOperatorAlreadyExists() {
        assertThrows(OperatorAlreadyExistsException.class, () -> gitService.addNewOperator(new Operator("TestPartner1")));
    }


    @Test
    void addNewOperatorSuccessfully() {
        assertDoesNotThrow(() -> gitService.addNewOperator(new Operator("NewOperator")));
        assertTrue(gitService.getAllOperators().contains(new Operator("NewOperator"))); // equals is overidden
    }
}