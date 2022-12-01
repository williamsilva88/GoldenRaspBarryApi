package com.api.goldenraspbarry;

import com.api.goldenraspbarry.domain.Indicated;
import com.api.goldenraspbarry.domain.Producer;
import com.api.goldenraspbarry.repositories.IndicatedRepository;
import com.api.goldenraspbarry.repositories.ProducerRepository;
import com.api.goldenraspbarry.services.impl.IndicatedServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;

import javax.transaction.Transactional;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.List;
import java.util.Optional;


@RunWith(SpringJUnit4ClassRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class GoldenRaspBarryApiApplicationTests {

	@InjectMocks
	private IndicatedServiceImpl indicatedServiceImpl;

	@Mock
	private ProducerRepository producerRepository;

	@Mock
	private IndicatedRepository indicatedRepository;

	private Optional<Producer> producer;
	private Optional<Indicated> indicated;

	private String dataCSV;

	@BeforeEach
	void setUp(){
		MockitoAnnotations.openMocks(this);
	}

	@Test
	@Transactional
	void whenGetCSVDataIndicated(){
		Mockito.when(producerRepository.findById(Mockito.anyInt())).thenReturn(producer);
		InputStream ips = getClass().getResourceAsStream("/dataCSV/movielist.csv");
		BufferedReader br = new BufferedReader(new InputStreamReader(ips));
		List<Indicated> indecated = indicatedServiceImpl.getCSVDataIndicated(br);
		Assertions.assertEquals(207, indecated.size());
		Assertions.assertEquals(1980, indecated.get(0).getYear());
		Assertions.assertEquals("Can't Stop the Music", indecated.get(0).getTitle());
		Assertions.assertEquals("Associated Film Distribution", indecated.get(0).getStudios());
		Assertions.assertEquals("Allan Carr", indecated.get(0).getProducers());
		Assertions.assertEquals(true, indecated.get(0).getWinner());
	}

	@Test
	void whenProducerFindById(){
		initDbList();
		Mockito.when(producerRepository.findById(Mockito.anyInt())).thenReturn(producer);
		Optional<Producer> resp = producerRepository.findById(1);
		Assertions.assertEquals(resp.get().getName(), "Jerry Bruckheimer");
	}

	@Test
	void whenIndicatedFindById(){
		initDbList();
		Mockito.when(indicatedRepository.findById(Mockito.anyInt())).thenReturn(indicated);
		Optional<Indicated> resp = indicatedRepository.findById(1);
		Assertions.assertEquals(resp.get().getTitle(), "Grown Ups 2");
	}

	private void initDbList(){
		producer = Optional.of(new Producer(1, "Jerry Bruckheimer"));
		indicated = Optional.of(new Indicated(1, 2013,"Grown Ups 2","Columbia Pictures",
				"Jack Giarraputo and Adam Sandler", false,
				Collections.singletonList(new Producer(1, "Jerry Bruckheimer"))));
	}
}
