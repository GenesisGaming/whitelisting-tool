package com.gn.whitelist.dao.git;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import com.gn.whitelist.exceptions.OperatorAlreadyExistsException;
import com.gn.whitelist.exceptions.OperatorMissingException;
import com.gn.whitelist.model.Operator;
import com.gn.whitelist.model.UpdateIpsRequest;
import io.quarkus.test.InjectMock;
import io.quarkus.test.component.QuarkusComponentTest;
import jakarta.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

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
        Files.write(customersDir.toPath().resolve("TestPartner1.conf"), "allow    192.168.0.3;\n".getBytes(), StandardOpenOption.APPEND);
        Files.write(customersDir.toPath().resolve("TestPartner2.conf"), "allow    192.168.0.4;\n".getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        Files.write(customersDir.toPath().resolve("TestPartner3.conf"), "allow    192.168.0.5;\n".getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        when(gitClient.getRepoPath()).thenReturn(testDir);
    }


    @Test
    void getOperatorIPsSuccessfully() {
        var ips = gitService.getOperatorIPs("TestPartner1");
        assertEquals(3, ips.size());
        assertTrue(ips.containsAll(List.of("192.168.0.1", "192.168.0.2", "192.168.0.3")));
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
        UpdateIpsRequest request = new UpdateIpsRequest();
        request.setIps((List.of("111.111.111.111", "222.222.222.222",  "222.222.222.222"))); // with a duplicate
        request.setWhitelistType(UpdateIpsRequest.WhitelistTypeEnum.API);
        request.setUpdateType(UpdateIpsRequest.UpdateTypeEnum.ADDITION);

        gitService.addNewIPs("TestPartner1", request);
        var updatedIPList = gitService.getOperatorIPs("TestPartner1");

        assertEquals(5, updatedIPList.size());
        assertTrue(updatedIPList.contains("111.111.111.111"));
        assertTrue(updatedIPList.contains("222.222.222.222"));
    }


    @Test
    void removeIPsFromExistingOperatorSuccessfully(){
        UpdateIpsRequest request = new UpdateIpsRequest();
        request.setIps((List.of("192.168.0.1", "192.168.0.1",  "192.168.0.2"))); // with a duplicate
        request.setWhitelistType(UpdateIpsRequest.WhitelistTypeEnum.API);
        request.setUpdateType(UpdateIpsRequest.UpdateTypeEnum.REMOVAL);

        gitService.removeIPs("TestPartner1", request);
        var updatedIPList = gitService.getOperatorIPs("TestPartner1");

        assertEquals(1, updatedIPList.size());
        assertTrue(updatedIPList.contains("192.168.0.3"));
    }

    @Test
    void addNewIPsThrowsOperatorDoesNotExist() {
        assertThrows(OperatorMissingException.class, () -> gitService.addNewIPs("NonExistingPartner", new UpdateIpsRequest()));
    }


    @Test
    void removeIPsThrowsOperatorDoesNotExist() {
        assertThrows(OperatorMissingException.class, () -> gitService.removeIPs("NonExistingPartner", new UpdateIpsRequest()));
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