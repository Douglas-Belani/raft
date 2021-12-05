//package com.example.restapifiletransfer;
//
//import com.example.restapifiletransfer.model.CanonicalHuffmanTree;
//import com.example.restapifiletransfer.model.HuffmanCode;
//import org.junit.jupiter.api.Test;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import java.util.ArrayList;
//import java.util.List;
//
//@SpringBootTest
//class RestApiFileTransferApplicationTests {
//
//	@Test
//	void contextLoads() {
//	}
//
//	@Test
//	void testCanonicalTreeOrdering() {
//		List<HuffmanCode> huffmanCodeList = new ArrayList<>();
//		huffmanCodeList.add(new HuffmanCode('A', "11"));
//		huffmanCodeList.add(new HuffmanCode('B', "0"));
//		huffmanCodeList.add(new HuffmanCode('C', "101"));
//		huffmanCodeList.add(new HuffmanCode('D', "100"));
//
//		CanonicalHuffmanTree canonicalHuffmanTree = new CanonicalHuffmanTree();
//		System.out.println(canonicalHuffmanTree.constructCanonicalTree(huffmanCodeList));
//	}
//
//}
