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

import javax.transaction.Transactional;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

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

    @Override
    public List<Indicated> getCSVDataIndicated(String fullPath) {
        return getCSVDataIndicated(fullPath, null);
    }

    public List<Indicated> getCSVDataIndicated(BufferedReader br) {
        return getCSVDataIndicated(null, br);
    }

    @Transactional
    public List<Indicated> getCSVDataIndicated(String fullPath, BufferedReader br) {
        String separetor = ";";
        int ignoreFisrtLines = 1;

        String linha = "";
        List<Indicated> registerList = new ArrayList<>();
        ArrayList<String> producers = new ArrayList<>();
        try {
            if(br == null) {
                br = new BufferedReader(new FileReader(fullPath));
            }
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
            //e.printStackTrace();
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
            for (int i = 0; i < producers.size(); i++) {
                producersListFinal.add(new Producer(i, producers.get(i)));
            }
            producers.forEach(name->{
                producersListFinal.add(new Producer(null, name));
            });
            producerRepository.saveAll(producersListFinal);
        }
        if(registerList.size() > 0) {
            indicatedRepository.saveAll(registerList);
        }

        return registerList;
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
        return getAwardsBreak(getIndicatorWin());
    }

    @Override
    public AwardsBreakDTO getAwardsBreak(List<Indicated> indicated) {
        AwardsBreakDTO awardsBreakDTO = new AwardsBreakDTO(new ArrayList<>(),new ArrayList<>());
        List<AwardedDTO> awardedDTOList = getAwardsList(indicated);

        if(awardedDTOList != null && awardedDTOList.size() > 0){
            Collections.sort(awardedDTOList);

            ArrayList<AwardedDTO> listMax  = new ArrayList<>();
            for (int i = 0; i < awardedDTOList.size(); i++) {
                if(validationAward(listMax, awardedDTOList.get(i))){
                    break;
                }
            }
            awardsBreakDTO.setMax(listMax);

            ArrayList<AwardedDTO> listMin  = new ArrayList<>();
            for (int i = awardedDTOList.size()-1; i >= 0 ; i--) {
                if(validationAward(listMin, awardedDTOList.get(i))){
                    break;
                }
            }
            awardsBreakDTO.setMin(listMin);

        }

        return awardsBreakDTO;
    }

    public boolean validationAward(ArrayList<AwardedDTO> list, AwardedDTO validation){
        if(list.size() > 0){
            if(list.get(0).getInterval() != validation.getInterval()){
                return true;
            }else{
                list.add(validation);
            }
        }else{
            list.add(validation);
        }
        return false;
    }

    public List<AwardedDTO> getAwardsList(List<Indicated> indicated) {
        List<AwardedDTO> awardedDTOList = new ArrayList<>();

        if(indicated !=null){
            Map<String, AwardedDTO> awardedLastDataMap = new HashMap<>();

            indicated.forEach(ind->{
                ind.getProducer().forEach(producer->{
                    AwardedDTO oldData = awardedLastDataMap.get(producer.getName());
                    if(oldData == null){
                        oldData = new AwardedDTO(producer.getName(), 0, ind.getYear(), 0);
                        awardedLastDataMap.put(producer.getName(), oldData);
                    }else{
                        if(oldData.getFollowingWin() == 0) {
                            oldData.setFollowingWin(ind.getYear());
                            oldData.setInterval(oldData.getFollowingWin() - oldData.getPreviousWin());
                        }else {
                            Integer newInterval = ind.getYear() - oldData.getFollowingWin();
                            oldData.setPreviousWin(oldData.getFollowingWin());
                            oldData.setFollowingWin(ind.getYear());
                            oldData.setInterval(newInterval);
                        }
                        awardedLastDataMap.put(producer.getName(), oldData);
                        awardedDTOList.add(new AwardedDTO(
                                oldData.getProducer(),
                                oldData.getInterval(),
                                oldData.getPreviousWin(),
                                oldData.getFollowingWin())
                        );
                    }
                });
            });
        }

        return awardedDTOList;
    }

    private List<Indicated> getIndicatorWin(){
        return indicatedRepository.findIndicatedWinner();
    }
}
