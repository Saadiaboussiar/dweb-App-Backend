package com.example.dweb_App.web.controllers;

import com.example.dweb_App.data.entities.BonIntervention;
import com.example.dweb_App.data.entities.Client;
import com.example.dweb_App.data.entities.Intervention;
import com.example.dweb_App.data.repositories.AdminRepository;
import com.example.dweb_App.data.repositories.BonInterventionRepository;
import com.example.dweb_App.data.repositories.ClientRepository;
import com.example.dweb_App.dto.response.ClientRentabilityDTO;
import com.example.dweb_App.dto.response.InterventionDetailDto;
import com.example.dweb_App.dto.response.PeriodeStatsDTO;
import com.example.dweb_App.dto.response.VilleRentabiliteDTO;
import com.example.dweb_App.exception.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;

@Slf4j
@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/admin")
@PreAuthorize("hasAuthority('ADMIN')")
public class AdminController {

    private static final Properties props = new Properties();
    private AdminRepository adminRepository;
    private ClientRepository clientRepository;
    private BonInterventionRepository bonInterventionRepository;

    public AdminController(AdminRepository adminRepository, ClientRepository clientRepository, BonInterventionRepository bonInterventionRepository) {
        this.adminRepository = adminRepository;
        this.clientRepository = clientRepository;
        this.bonInterventionRepository = bonInterventionRepository;
    }


    @GetMapping("clients-rentability/{periode}")
    public ResponseEntity<List<ClientRentabilityDTO>> getClientRentability(@PathVariable String periode){

        List<Client> clients=clientRepository.findAll();
        List<ClientRentabilityDTO> clientRentabilityDTOList=new ArrayList<>();
        for(Client client: clients){

            Collection<BonIntervention> bonInterventions=client.getBonInterventions();
            int nomberIntervention=bonInterventions.size();
            double kms=bonInterventions.stream()
                    .map(BonIntervention :: getKm)
                    .reduce(0.0,Double::sum);

            double costTransport= kms * Double.parseDouble(props.getProperty("TAUX_KM", "0"));
            double kmMeanParIntervention= kms/nomberIntervention;
            double costParIntervention= costTransport/nomberIntervention;

            ClientRentabilityDTO clientRentabilityDTO= ClientRentabilityDTO.builder()
                    .cin(client.getCin())
                    .ville(client.getVille())
                    .fullName(client.getFullName())
                    .nbInterventions(nomberIntervention)
                    .totalKm(kms)
                    .coutTransport(costTransport)
                    .kmMoyenParIntervention(kmMeanParIntervention)
                    .coutParIntervention(costParIntervention).build();
            clientRentabilityDTOList.add(clientRentabilityDTO);
        }

        return ResponseEntity.ok(clientRentabilityDTOList);
    }

    @GetMapping("/villes-rentability/{periode}")
    public ResponseEntity<List<VilleRentabiliteDTO>> getCityRentability(@PathVariable String periode){

        List<Client> clients=clientRepository.findAll();
        List<VilleRentabiliteDTO> villeRentabiliteDTOS=new ArrayList<>();
        List<String> cities=new ArrayList<>();

        for(Client client:clients){
            String city=client.getVille();
            if(!cities.contains(city)){
                cities.add(city);
            }
        }

        for(String city: cities){
            List<Client> clientList= clients.stream()
                    .filter(client -> city.equals(client.getVille()))
                    .toList();
            int nbreClients= clientList.size();

            int nbreInterventions= Long.bitCount(clientList.stream()
                    .map(Client::getBonInterventions)
                    .count());

            double totalKm= clientList.stream()
                    .map(Client::getBonInterventions)
                    .flatMap(Collection::stream)
                    .mapToDouble(BonIntervention::getKm)
                    .sum();

            VilleRentabiliteDTO villeRentabiliteDTO=VilleRentabiliteDTO.builder()
                    .name(city)
                    .nbClients(nbreClients)
                    .nbInterventions(nbreInterventions)
                    .totalKm(totalKm).build();
            villeRentabiliteDTOS.add(villeRentabiliteDTO);
        }
        return ResponseEntity.ok(villeRentabiliteDTOS);
    }

    @GetMapping("/periodes-rentabilite/{periode}")
    public ResponseEntity<List<PeriodeStatsDTO>> getPeriodeStats(@PathVariable String periode){

        String[][] moisComplets = {
                {"1", "Janvier"}, {"2", "Février"}, {"3", "Mars"}, {"4", "Avril"},
                {"5", "Mai"}, {"6", "Juin"}, {"7", "Juillet"}, {"8", "Août"},
                {"9", "Septembre"}, {"10", "Octobre"}, {"11", "Novembre"}, {"12", "Décembre"}
        };
        List<PeriodeStatsDTO> periodeStatsDTOS=new ArrayList<>();

        String currentYear = String.valueOf((LocalDate.now().getYear()));

        for(String[] month:moisComplets){

            List<BonIntervention> bonInterventions=bonInterventionRepository.findByYearAndMonth(currentYear,month[0]);
            int nbreInterventions= bonInterventions.size();

            double kmsMonth= bonInterventions.stream()
                    .map(BonIntervention::getKm)
                    .reduce(0.0,Double::sum);

            double costTransport = kmsMonth * Double.parseDouble(props.getProperty("TAUX_KM", "0"));

            PeriodeStatsDTO periodeStatsDTO= PeriodeStatsDTO.builder()
                    .mois(month[1]+"-"+currentYear)
                    .KmTotal(kmsMonth)
                    .nbInterventions(nbreInterventions)
                    .coutTransport(costTransport).build();
            periodeStatsDTOS.add(periodeStatsDTO);

        }
        return ResponseEntity.ok(periodeStatsDTOS);
    }

    @GetMapping("/client-interventions/{clientCin}/{period}")
    public ResponseEntity<List<InterventionDetailDto>> getClientInterventions(@PathVariable String clientCin, @PathVariable String period){

        Client client=clientRepository.findByCin(clientCin)
                .orElseThrow(()->new EntityNotFoundException("Client not Found "+clientCin));
        List<BonIntervention> bonInterventions=client.getBonInterventions().stream().toList();
        List<InterventionDetailDto> interventionDetailDtos=new ArrayList<>();

        for(BonIntervention bonIntervention:bonInterventions){

            String status = null;
            if (bonIntervention.getIntervention() != null) {
                status = bonIntervention.getIntervention().getStatus().toString();
            }
            InterventionDetailDto interventionDetailDto= InterventionDetailDto.builder()
                    .id(bonIntervention.getId())
                    .date(bonIntervention.getDate())
                    .distanceKm(bonIntervention.getKm())
                    .status(status)
                    .technicianName(bonIntervention.getTechnician().getFirstName() + ' ' + bonIntervention.getTechnician().getLastName()).build();
            interventionDetailDtos.add(interventionDetailDto);
        }
        return  ResponseEntity.ok(interventionDetailDtos);
    }



}
