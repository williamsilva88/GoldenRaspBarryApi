package com.api.goldenraspbarry;

import com.api.goldenraspbarry.domain.Indicated;
import com.api.goldenraspbarry.domain.Producer;
import com.api.goldenraspbarry.repositories.IndicatedRepository;
import com.api.goldenraspbarry.repositories.ProducerRepository;
import com.api.goldenraspbarry.services.impl.IndicatedServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@SpringBootTest
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
	void whenGetCSVDataIndicated(){
		Mockito.when(producerRepository.findById(Mockito.anyInt())).thenReturn(producer);
		initDataTest();
		InputStream stream = new ByteArrayInputStream(dataCSV.getBytes
				(Charset.forName("UTF-8")));
		BufferedReader br = new BufferedReader(new InputStreamReader(stream));
		indicatedServiceImpl.getCSVDataIndicated(br);
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

	private void initDataTest(){
		dataCSV = "year;title;studios;producers;winner\n2013;Grown Ups 2;Columbia Pictures;Jack Giarraputo and Adam Sandler;\n" +
				"2013;The Lone Ranger;Walt Disney Pictures;Jerry Bruckheimer and Gore Verbinski;\n" +
				"2013;A Madea Christmas;Lionsgate;Ozzie Areu, Matt Moore and Tyler Perry;\n" +
				"2014;Saving Christmas;Samuel Goldwyn Films;Darren Doane, Raphi Henley, Amanda Rosser and David Shannon;yes\n" +
				"2014;Left Behind;Freestyle Releasing, Entertainment One;Michael Walker and Paul LaLonde;\n" +
				"2014;The Legend of Hercules;Summit Entertainment;Boaz Davidson, Renny Harlin, Danny Lerner and Les Weldon;\n" +
				"2014;Teenage Mutant Ninja Turtles;Paramount Pictures, Nickelodeon Movies, Platinum Dunes;Michael Bay, Ian Bryce, Andrew Form, Bradley Fuller, Scott Mednick and Galen Walker;\n" +
				"2014;Transformers: Age of Extinction;Paramount Pictures;Ian Bryce, Tom DeSanto, Lorenzo di Bonaventura and Don Murphy;\n" +
				"2015;Fantastic Four;20th Century Fox;Simon Kinberg, Matthew Vaughn, Hutch Parker, Robert Kulzer and Gregory Goodman;yes\n" +
				"2015;Fifty Shades of Grey;Universal Pictures, Focus Features;Michael De Luca, Dana Brunetti and E. L. James;yes\n" +
				"2015;Jupiter Ascending;Warner Bros.;Grant Hill and The Wachowskis;\n" +
				"2015;Paul Blart: Mall Cop 2;Columbia Pictures;Todd Garner, Kevin James and Adam Sandler;\n" +
				"2015;Pixels;Columbia Pictures;Adam Sandler, Chris Columbus, Mark Radcliffe and Allen Covert;\n" +
				"2016;Hillary's America: The Secret History of the Democratic Party;Quality Flix;Gerald R. Molen;yes\n" +
				"2016;Batman v Superman: Dawn of Justice;Warner Bros.;Charles Roven and Deborah Snyder;\n" +
				"2016;Dirty Grandpa;Lionsgate;Bill Block, Michael Simkin, Jason Barrett and Barry Josephson;\n" +
				"2016;Gods of Egypt;Summit Entertainment;Basil Iwanyk and Alex Proyas;\n" +
				"2016;Independence Day: Resurgence;20th Century Fox;Dean Devlin, Harald Kloser and Roland Emmerich;\n" +
				"2016;Zoolander 2;Paramount Pictures;Stuart Cornfeld, Scott Rudin, Ben Stiller and Clayton Townsend;\n" +
				"2017;The Emoji Movie;Columbia Pictures;Michelle Raimo Kouyate;yes\n" +
				"2017;Baywatch;Paramount Pictures;Ivan Reitman, Michael Berk, Douglas Schwartz, Gregory J. Bonann and Beau Flynn;\n" +
				"2017;Fifty Shades Darker;Universal Pictures;Michael De Luca, E. L. James, Dana Brunetti and Marcus Viscidi;\n" +
				"2017;The Mummy;Universal Pictures;Alex Kurtzman, Chris Morgan, Sean Daniel and Sarah Bradshaw;\n" +
				"2017;Transformers: The Last Knight;Paramount Pictures;Don Murphy, Tom DeSanto, Lorenzo di Bonaventura and Ian Bryce;\n" +
				"2018;Holmes & Watson;Columbia Pictures;Will Ferrell, Adam McKay, Jimmy Miller and Clayton Townsend;yes\n" +
				"2018;Gotti;Vertical Entertainment;Randall Emmett, Marc Fiore, Michael Froch and George Furla;\n" +
				"2018;The Happytime Murders;STX;Ben Falcone, Jeffrey Hayes, Brian Henson and Melissa McCarthy;\n" +
				"2018;Robin Hood;Summit Entertainment;Jennifer Davisson and Leonardo DiCaprio;\n" +
				"2018;Winchester;Lionsgate;Tim McGahan and Brett Tomberlin;\n" +
				"2019;Cats;Universal Pictures;Debra Hayward, Tim Bevan, Eric Fellner, and Tom Hooper;yes\n" +
				"2019;The Fanatic;Quiver Distribution;Daniel Grodnik, Oscar Generale, and Bill Kenwright;\n" +
				"2019;The Haunting of Sharon Tate;Saban Films;Lucas Jarach, Daniel Farrands, and Eric Brenner;\n" +
				"2019;A Madea Family Funeral;Lionsgate;Ozzie Areu, Will Areu, and Mark E. Swinton;\n" +
				"2019;Rambo: Last Blood;Lionsgate;Avi Lerner, Kevin King Templeton, Yariv Lerner, and Les Weldon;";
	}

}
