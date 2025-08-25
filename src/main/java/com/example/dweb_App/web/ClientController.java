package com.example.dweb_App.web;

import com.example.dweb_App.data.entities.Client;
import com.example.dweb_App.data.service.ClientService;
import com.example.dweb_App.dto.request.ClientUpdateDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/clientInfos")

public class ClientController {

    private ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @PostMapping
    public ResponseEntity<?> saveClientInfos(@RequestBody Client client) {

        if (clientService.loadClient(client.getFullName()) == null) {
            clientService.addNewClient(client);
            return ResponseEntity.ok("Client saved successfully");
        } else {
            return ResponseEntity.ok("This client already exists");
        }
    }

    @GetMapping
    public ResponseEntity<List<Client>> getClients() {
        List<Client> clients= clientService.allClients();
        if(!clients.isEmpty()){
            return ResponseEntity.ok(clients);
        }
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping(path="/{clientCin}")
    public ResponseEntity<?> deleteClient(@PathVariable String clientCin) {
        Client client = clientService.loadClientByCin(clientCin);
        if (client == null) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Client not found"));
        }

        clientService.deleteClientByCin(clientCin);

        return ResponseEntity.ok(Map.of(
                "message", "Client deleted successfully",
                "clientName", client.getFullName()
        ));
    }

    @PutMapping(path="/{clientCin}")
    public ResponseEntity<?> editClientInfos(@PathVariable String clientCin,@RequestBody ClientUpdateDto client){
        Client existingClient=clientService.loadClientByCin(clientCin);
        if (existingClient == null) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Client not found"));
        }
        try{
            existingClient.setEmail(client.getEmail());
            existingClient.setPhoneNumber(client.getPhoneNumber());
            existingClient.setAdresse(client.getAdresse());
            existingClient.setVille(client.getVille());
            existingClient.setFullName(client.getFullName());
            existingClient.setContrat(client.getContrat());
            existingClient.setReseauSocial(client.getReseauSocial());

            log.info("Updating client {} (CIN: {})", existingClient.getFullName(), clientCin);

            Client updatedClient = clientService.saveClient(existingClient);

            return ResponseEntity.ok(updatedClient);
        }catch (Exception e){
            log.error("Failed to update client CIN: {}", clientCin, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error while editing client infos: " + e.getMessage());
        }
    }
}
