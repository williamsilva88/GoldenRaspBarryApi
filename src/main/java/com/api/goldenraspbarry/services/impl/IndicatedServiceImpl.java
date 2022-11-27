package com.api.goldenraspbarry.services.impl;

import com.api.goldenraspbarry.domain.DTO.AwardedDTO;
import com.api.goldenraspbarry.domain.DTO.AwardsBreakDTO;
import com.api.goldenraspbarry.domain.Indicated;
import com.api.goldenraspbarry.domain.Producer;
import com.api.goldenraspbarry.repositories.IndicatedRepository;
import com.api.goldenraspbarry.repositories.ProducerRepository;
import com.api.goldenraspbarry.services.IndicatedService;
import com.api.goldenraspbarry.services.exceptions.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class IndicatedServiceImpl implements IndicatedService {

    @Autowired
    private IndicatedRepository indicatedRepository;

    @Autowired
    private ProducerRepository producerRepository;

    @Override
    public Indicated findById(Integer id) {
        Optional<Indicated> indicatedOptional = indicatedRepository.findById(id);
        return indicatedOptional.orElseThrow(() ->new ObjectNotFoundException("Objeto não encontrado"));
    }

    public void getCSVDataIndicated() {
        String path = this.getClass().getResource("../../../../../").getPath();
        String system = this.getClass().getResource("").getPath().contains("\\")?"\\":"/";
        String archive = "dataCSV"+system+"movielist.csv";
        String fullPath = path + archive;
        String separetor = ";";
        int ignoreFisrtLines = 1;

        BufferedReader br = null;
        String linha = "";
        List<Indicated> registerList = new ArrayList<>();
        ArrayList<String> producers = new ArrayList<>();
        try {

            br = new BufferedReader(new FileReader(fullPath));
            int lines = 0;
            while ((linha = br.readLine()) != null) {
                lines++;
                if(lines > ignoreFisrtLines) {
                    String[] dataList = linha.split(separetor);
                    Indicated register = new Indicated(
                            null,
                            getInfoInteger(dataList, 0),
                            getInfoString(dataList, 1),
                            getInfoString(dataList, 2),
                            getInfoString(dataList, 3),
                            getInfoBoolean(dataList, 4),
                            null
                    );
                    registerList.add(register);
                    ArrayList<Integer> relations = generateProducers(producers, getInfoString(dataList, 3));
                    if(relations.size() > 0){
                        ArrayList<Producer> listProducers = new ArrayList<>();
                        relations.forEach(relProducerId->{
                            listProducers.add(new Producer(relProducerId+1, producers.get(relProducerId)));
                        });
                        register.setProducer(listProducers);
                    }
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        if(producers.size() > 0){
            ArrayList<Producer> producersListFinal = new ArrayList<>();
            producers.forEach(name->{
                producersListFinal.add(new Producer(null, name));
            });
            producerRepository.saveAll(producersListFinal);
        }
        if(registerList.size() > 0) {
            indicatedRepository.saveAll(registerList);
        }
    }

    private ArrayList<Integer> generateProducers(ArrayList<String> producer, String data){
        String[] p1 = data.split(",");
        ArrayList<Integer> relation = new ArrayList<Integer>();
        for (int i = 0; i < p1.length; i++) {
            String[] p2 = p1[i].split(" and ");
            for (int i2 = 0; i2 < p2.length; i2++) {
                if(p2[i2] != null && p2[i2].trim() != "") {
                    if (!producer.contains(p2[i2].trim())) {
                        producer.add(p2[i2].trim());
                    }

                    int idx = producer.indexOf(p2[i2].trim());
                    if (!relation.contains(idx)) {
                        relation.add(idx);
                    }
                }
            }
        }
        return relation;
    }

    private String getInfoString(String[] data, int index){
        String info = null;
        try {
            info = data[index];
            return info;
        }catch (Exception e) { }

        return "";
    }

    private Integer getInfoInteger(String[] data, int index){
        String info = null;
        try {
            info = data[index];
            return Integer.parseInt(info);
        }catch (Exception e) { }

        return null;
    }

    private Boolean getInfoBoolean(String[] data, int index){
        String info = null;
        try {
            info = data[index];
            return info.equalsIgnoreCase("yes") ? true : false;
        }catch (Exception e) { }

        return false;
    }

    @Override
    public AwardsBreakDTO findAwardsBreak() {
        List<Indicated> indicated = getIndicatorWin();

        List<AwardedDTO> awardedDTOListTemp = new ArrayList<>();
        List<AwardedDTO> awardedDTOListMaxes = new ArrayList<>();
        List<AwardedDTO> awardedDTOListMin = new ArrayList<>();

        indicated.forEach(ind->{
            ind.getProducer().forEach(prod->{
                AwardedDTO retOld = filterAwarded(awardedDTOListTemp, prod.getName());

                if(retOld == null){
                    awardedDTOListTemp.add(new AwardedDTO(prod.getName(), 0, ind.getYear(), 0));
                    awardedDTOListMaxes.add(new AwardedDTO(prod.getName(), 0, ind.getYear(), 0));
                    awardedDTOListMin.add(new AwardedDTO(prod.getName(), 999999999, ind.getYear(), 0));
                }else{
                    AwardedDTO awardedDTOValidMax = filterAwarded(awardedDTOListMaxes, prod.getName());
                    AwardedDTO awardedDTOValidMin = filterAwarded(awardedDTOListMin, prod.getName());

                    if(retOld.getFollowingWin() == 0){
                        retOld.setFollowingWin(ind.getYear());
                        retOld.setInterval(retOld.getFollowingWin() - retOld.getPreviousWin());

                        awardedDTOValidMax.setInterval(retOld.getInterval());
                        awardedDTOValidMax.setFollowingWin(retOld.getFollowingWin());
                        awardedDTOValidMin.setInterval(retOld.getInterval());
                        awardedDTOValidMin.setFollowingWin(retOld.getFollowingWin());
                    }else{
                        Integer newInterval = ind.getYear() - retOld.getFollowingWin();

                        if(awardedDTOValidMax.getInterval() < newInterval){
                            awardedDTOValidMax.setFollowingWin(ind.getYear());
                            awardedDTOValidMax.setPreviousWin(retOld.getFollowingWin());
                            awardedDTOValidMax.setInterval(newInterval);
                        }

                        if(awardedDTOValidMin.getInterval() > newInterval){
                            awardedDTOValidMin.setFollowingWin(ind.getYear());
                            awardedDTOValidMin.setPreviousWin(retOld.getFollowingWin());
                            awardedDTOValidMin.setInterval(newInterval);
                        }

                        retOld.setPreviousWin(retOld.getFollowingWin());
                        retOld.setFollowingWin(ind.getYear());
                        retOld.setInterval(newInterval);
                    }
                }
            });
        });

        awardedDTOListMaxes.forEach(max->{
            if(max.getFollowingWin() == 0){
                max.setFollowingWin(max.getPreviousWin());
            }
            if(max.getInterval() == 0){
                max.setInterval(1);
            }
        });

        awardedDTOListMin.forEach(min->{
            if(min.getFollowingWin() == 0){
                min.setFollowingWin(min.getPreviousWin());
            }
        });

        return genereteAwardsBreak(awardedDTOListMaxes, awardedDTOListMin);
    }

    private AwardsBreakDTO genereteAwardsBreak(List<AwardedDTO> awardedDTOListMaxes, List<AwardedDTO> awardedDTOListMin){
        AwardsBreakDTO awardsBreakDTO = new AwardsBreakDTO(new ArrayList<>(),new ArrayList<>());

        if(awardedDTOListMaxes != null && awardedDTOListMaxes.size() > 0){
            Collections.sort(awardedDTOListMaxes);
            ArrayList<AwardedDTO> listMax  = new ArrayList<>();
            for (int i = 0; i < awardedDTOListMaxes.size(); i++) {
                if(listMax.size() > 0){
                    if(listMax.get(0).getInterval() != awardedDTOListMaxes.get(i).getInterval()){
                        break;
                    }else{
                        listMax.add(awardedDTOListMaxes.get(i));
                    }
                }else{
                    listMax.add(awardedDTOListMaxes.get(i));
                }
            }
            awardsBreakDTO.setMax(listMax);
        }

        if(awardedDTOListMaxes != null && awardedDTOListMaxes.size() > 0){
            Collections.sort(awardedDTOListMin);
            ArrayList<AwardedDTO> listMin  = new ArrayList<>();
            for (int i = awardedDTOListMin.size()-1; i >= 0 ; i--) {
                if(listMin.size() > 0){
                    if(listMin.get(0).getInterval() != awardedDTOListMin.get(i).getInterval()){
                        break;
                    }else{
                        listMin.add(awardedDTOListMin.get(i));
                    }
                }else{
                    listMin.add(awardedDTOListMin.get(i));
                }
            }
            awardsBreakDTO.setMin(listMin);
        }

        return awardsBreakDTO;
    }

    private AwardedDTO filterAwarded(List<AwardedDTO> list, String name){
        List<AwardedDTO> retOldList = list.stream().filter(aw -> aw.getProducer() == name)
                .collect(Collectors.toList());
        AwardedDTO retOld = null;
        if(retOldList != null && retOldList.size() > 0){
            retOld = retOldList.get(0);
        }
        return retOld;
    }

    private List<Indicated> getIndicatorWin(){
        return indicatedRepository.findIndicatedWinner();
    }
}
