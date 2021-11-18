package it.pyrox.jammer.core.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.Test;

import it.pyrox.jammer.core.enums.SaveType;
import it.pyrox.jammer.core.model.Block;
import it.pyrox.jammer.core.model.MemoryCard;

public class MemoryCardControllerTest {  
	
	@Test
    public void testMemoryCardFromFileSystem() throws IOException {
    	File file = new File(getClass().getClassLoader().getResource("Memorycard1.mcr").getFile());
		MemoryCard memoryCard = MemoryCardController.getInstance(file);			
		System.out.println(memoryCard);	             
		assertNotNull(memoryCard);
		assertNotNull(memoryCard.getBlocks());
		assertEquals("SLES-02562", memoryCard.getBlockAt(0).getProductCode());
		assertEquals("SLUS-00664", memoryCard.getBlockAt(1).getProductCode());
		assertEquals("SLUS-00664", memoryCard.getBlockAt(2).getProductCode());
		assertEquals("SLUS-00923", memoryCard.getBlockAt(3).getProductCode());
		assertEquals("SCESP03046", memoryCard.getBlockAt(4).getProductCode());
		assertEquals("SLES-02562", memoryCard.getBlockAt(5).getProductCode());
		assertEquals("SLES-02562", memoryCard.getBlockAt(6).getProductCode());
		assertEquals("SLES-02562", memoryCard.getBlockAt(7).getProductCode());
		assertEquals("SLES-02562", memoryCard.getBlockAt(8).getProductCode());
		assertEquals("SLES-02210", memoryCard.getBlockAt(9).getProductCode());
		assertEquals("SLUS-00986", memoryCard.getBlockAt(10).getProductCode());
		assertEquals("SLUS-01212", memoryCard.getBlockAt(11).getProductCode());
		assertEquals("SCUS-94900", memoryCard.getBlockAt(12).getProductCode());
		assertEquals("SLES-02533", memoryCard.getBlockAt(13).getProductCode());
		assertEquals("SLES-02533", memoryCard.getBlockAt(14).getProductCode());
    }
    
	@Test
    public void testMemoryCardFromByteArray() throws IOException {
		File file = new File(getClass().getClassLoader().getResource("Memorycard1.mcr").getFile());
		BufferedInputStream input = new BufferedInputStream(new FileInputStream(file));
		int bytesRead = 1;
		byte[] byteArray = new byte[256000];
		while (bytesRead > 0) {
          bytesRead = input.read(byteArray); 
        }
		input.close();
		MemoryCard memoryCard = MemoryCardController.getInstance(byteArray);
		System.out.println(memoryCard);
		assertNotNull(memoryCard);
		assertNotNull(memoryCard.getBlocks());
		assertEquals("SLES-02562", memoryCard.getBlockAt(0).getProductCode());
		assertEquals("SLUS-00664", memoryCard.getBlockAt(1).getProductCode());
		assertEquals("SLUS-00664", memoryCard.getBlockAt(2).getProductCode());
		assertEquals("SLUS-00923", memoryCard.getBlockAt(3).getProductCode());
		assertEquals("SCESP03046", memoryCard.getBlockAt(4).getProductCode());
		assertEquals("SLES-02562", memoryCard.getBlockAt(5).getProductCode());
		assertEquals("SLES-02562", memoryCard.getBlockAt(6).getProductCode());
		assertEquals("SLES-02562", memoryCard.getBlockAt(7).getProductCode());
		assertEquals("SLES-02562", memoryCard.getBlockAt(8).getProductCode());
		assertEquals("SLES-02210", memoryCard.getBlockAt(9).getProductCode());
		assertEquals("SLUS-00986", memoryCard.getBlockAt(10).getProductCode());
		assertEquals("SLUS-01212", memoryCard.getBlockAt(11).getProductCode());
		assertEquals("SCUS-94900", memoryCard.getBlockAt(12).getProductCode());
		assertEquals("SLES-02533", memoryCard.getBlockAt(13).getProductCode());
		assertEquals("SLES-02533", memoryCard.getBlockAt(14).getProductCode());
    }
	
	@Test
	public void testFindLinkedBlocksWhenNoLinkedBlock() throws IOException {
    	File file = new File(getClass().getClassLoader().getResource("Memorycard3.mcr").getFile());
		MemoryCard memoryCard = MemoryCardController.getInstance(file);			
		System.out.println(memoryCard);			               
		assertNotNull(memoryCard);
		assertNotNull(memoryCard.getBlocks());
		assertEquals(SaveType.INITIAL, memoryCard.getBlockAt(0).getSaveType());
		List<Block> result = MemoryCardController.findLinkedBlocks(memoryCard, 0);
		assertNotNull(result);
		assertEquals(1, result.size());
		assertEquals(memoryCard.getBlockAt(0), result.get(0));		
	}
	
	@Test
	public void testFindLinkedBlocksWhenInitial() throws IOException {
    	File file = new File(getClass().getClassLoader().getResource("Memorycard3.mcr").getFile());
		MemoryCard memoryCard = MemoryCardController.getInstance(file);			
		System.out.println(memoryCard);			               
		assertNotNull(memoryCard);
		assertNotNull(memoryCard.getBlocks());
		assertEquals(SaveType.INITIAL, memoryCard.getBlockAt(1).getSaveType());
		assertEquals(SaveType.END_LINK, memoryCard.getBlockAt(2).getSaveType());
		List<Block> result = MemoryCardController.findLinkedBlocks(memoryCard, 1);
		assertNotNull(result);
		assertEquals(2, result.size());
		assertEquals(memoryCard.getBlockAt(1), result.get(0));
		assertEquals(memoryCard.getBlockAt(2), result.get(1));
	}
	
	@Test
	public void testFindLinkedBlocksWhenMiddle() throws IOException {
    	File file = new File(getClass().getClassLoader().getResource("Memorycard3.mcr").getFile());
		MemoryCard memoryCard = MemoryCardController.getInstance(file);			
		System.out.println(memoryCard);			               
		assertNotNull(memoryCard);
		assertNotNull(memoryCard.getBlocks());
		assertEquals(SaveType.INITIAL, memoryCard.getBlockAt(6).getSaveType());
		assertEquals(SaveType.MIDDLE_LINK, memoryCard.getBlockAt(7).getSaveType());
		assertEquals(SaveType.END_LINK, memoryCard.getBlockAt(8).getSaveType());
		List<Block> result = MemoryCardController.findLinkedBlocks(memoryCard, 7);
		assertNotNull(result);
		assertEquals(3, result.size());
		assertEquals(memoryCard.getBlockAt(6), result.get(0));
		assertEquals(memoryCard.getBlockAt(7), result.get(1));
		assertEquals(memoryCard.getBlockAt(8), result.get(2));
	}
	
	@Test
	public void testFindLinkedBlocksWhenEnd() throws IOException {
    	File file = new File(getClass().getClassLoader().getResource("Memorycard3.mcr").getFile());
		MemoryCard memoryCard = MemoryCardController.getInstance(file);			
		System.out.println(memoryCard);			               
		assertNotNull(memoryCard);
		assertNotNull(memoryCard.getBlocks());
		assertEquals(SaveType.INITIAL, memoryCard.getBlockAt(6).getSaveType());
		assertEquals(SaveType.MIDDLE_LINK, memoryCard.getBlockAt(7).getSaveType());
		assertEquals(SaveType.END_LINK, memoryCard.getBlockAt(8).getSaveType());
		List<Block> result = MemoryCardController.findLinkedBlocks(memoryCard, 8);
		assertNotNull(result);
		assertEquals(3, result.size());
		assertEquals(memoryCard.getBlockAt(6), result.get(0));
		assertEquals(memoryCard.getBlockAt(7), result.get(1));
		assertEquals(memoryCard.getBlockAt(8), result.get(2));
	}
	
	@Test
	public void testFindLinkedBlocksWhenFormatted() throws IOException {
    	File file = new File(getClass().getClassLoader().getResource("Memorycard3.mcr").getFile());
		MemoryCard memoryCard = MemoryCardController.getInstance(file);			
		System.out.println(memoryCard);			               
		assertNotNull(memoryCard);
		assertNotNull(memoryCard.getBlocks());
		assertEquals(SaveType.FORMATTED, memoryCard.getBlockAt(10).getSaveType());
		List<Block> result = MemoryCardController.findLinkedBlocks(memoryCard, 10);
		assertNotNull(result);
		assertEquals(0, result.size());		
	}
	
	@Test
	public void testToggleDeletedBlockWhenNoLinkedThenSingleInitialDeleted() throws IOException {
    	File file = new File(getClass().getClassLoader().getResource("Memorycard3.mcr").getFile());
		MemoryCard memoryCard = MemoryCardController.getInstance(file);			
		System.out.println("Before:");
		System.out.println(memoryCard);			               
		assertNotNull(memoryCard);
		assertNotNull(memoryCard.getBlocks());
		assertEquals(SaveType.INITIAL, memoryCard.getBlockAt(0).getSaveType());
		MemoryCardController.toggleSaveTypeDeleted(memoryCard, 0);
		System.out.println("After:");
		System.out.println(memoryCard);		
		assertNotNull(memoryCard);
		assertNotNull(memoryCard.getBlocks());
		assertEquals(SaveType.INITIAL_DELETED, memoryCard.getBlockAt(0).getSaveType());
	}
	
	@Test
	public void testToggleDeletedBlockWhenNoLinkedDeletedThenSingleInitial() throws IOException {
    	File file = new File(getClass().getClassLoader().getResource("Memorycard3_deleted.mcr").getFile());
		MemoryCard memoryCard = MemoryCardController.getInstance(file);			
		System.out.println("Before:");
		System.out.println(memoryCard);			               
		assertNotNull(memoryCard);
		assertNotNull(memoryCard.getBlocks());
		assertEquals(SaveType.INITIAL_DELETED, memoryCard.getBlockAt(0).getSaveType());
		MemoryCardController.toggleSaveTypeDeleted(memoryCard, 0);
		System.out.println("After:");
		System.out.println(memoryCard);		
		assertNotNull(memoryCard);
		assertNotNull(memoryCard.getBlocks());
		assertEquals(SaveType.INITIAL, memoryCard.getBlockAt(0).getSaveType());
	}
	
	@Test
	public void testToggleDeletedBlockWhenInitialLinkedThenLinkedDeleted() throws IOException {
    	File file = new File(getClass().getClassLoader().getResource("Memorycard3.mcr").getFile());
		MemoryCard memoryCard = MemoryCardController.getInstance(file);			
		System.out.println("Before:");
		System.out.println(memoryCard);			               
		assertNotNull(memoryCard);
		assertNotNull(memoryCard.getBlocks());
		assertEquals(SaveType.INITIAL, memoryCard.getBlockAt(1).getSaveType());
		assertEquals(SaveType.END_LINK, memoryCard.getBlockAt(2).getSaveType());
		MemoryCardController.toggleSaveTypeDeleted(memoryCard, 1);
		System.out.println("After:");
		System.out.println(memoryCard);		
		assertNotNull(memoryCard);
		assertNotNull(memoryCard.getBlocks());
		assertEquals(SaveType.INITIAL_DELETED, memoryCard.getBlockAt(1).getSaveType());
		assertEquals(SaveType.END_LINK_DELETED, memoryCard.getBlockAt(2).getSaveType());
	}
	
	@Test
	public void testToggleDeletedBlockWhenInitialLinkedDeletedThenLinkedNotDeleted() throws IOException {
    	File file = new File(getClass().getClassLoader().getResource("Memorycard3_deleted.mcr").getFile());
		MemoryCard memoryCard = MemoryCardController.getInstance(file);			
		System.out.println("Before:");
		System.out.println(memoryCard);			               
		assertNotNull(memoryCard);
		assertNotNull(memoryCard.getBlocks());
		assertEquals(SaveType.INITIAL_DELETED, memoryCard.getBlockAt(1).getSaveType());
		assertEquals(SaveType.END_LINK_DELETED, memoryCard.getBlockAt(2).getSaveType());
		MemoryCardController.toggleSaveTypeDeleted(memoryCard, 1);
		System.out.println("After:");
		System.out.println(memoryCard);		
		assertNotNull(memoryCard);
		assertNotNull(memoryCard.getBlocks());
		assertEquals(SaveType.INITIAL, memoryCard.getBlockAt(1).getSaveType());
		assertEquals(SaveType.END_LINK, memoryCard.getBlockAt(2).getSaveType());
	}
	
	@Test
	public void testToggleDeletedBlockWheMiddleLinkedThenLinkedDeleted() throws IOException {
    	File file = new File(getClass().getClassLoader().getResource("Memorycard3.mcr").getFile());
		MemoryCard memoryCard = MemoryCardController.getInstance(file);			
		System.out.println("Before:");
		System.out.println(memoryCard);			               
		assertNotNull(memoryCard);
		assertNotNull(memoryCard.getBlocks());
		assertEquals(SaveType.INITIAL, memoryCard.getBlockAt(6).getSaveType());
		assertEquals(SaveType.MIDDLE_LINK, memoryCard.getBlockAt(7).getSaveType());
		assertEquals(SaveType.END_LINK, memoryCard.getBlockAt(8).getSaveType());
		MemoryCardController.toggleSaveTypeDeleted(memoryCard, 7);
		System.out.println("After:");
		System.out.println(memoryCard);		
		assertNotNull(memoryCard);
		assertEquals(SaveType.INITIAL_DELETED, memoryCard.getBlockAt(6).getSaveType());
		assertEquals(SaveType.MIDDLE_LINK_DELETED, memoryCard.getBlockAt(7).getSaveType());
		assertEquals(SaveType.END_LINK_DELETED, memoryCard.getBlockAt(8).getSaveType());
	}
	
	@Test
	public void testToggleDeletedBlockWheMiddleLinkedDeletedThenLinkedNotDeleted() throws IOException {
    	File file = new File(getClass().getClassLoader().getResource("Memorycard3_deleted.mcr").getFile());
		MemoryCard memoryCard = MemoryCardController.getInstance(file);			
		System.out.println("Before:");
		System.out.println(memoryCard);			               
		assertNotNull(memoryCard);
		assertNotNull(memoryCard.getBlocks());
		assertEquals(SaveType.INITIAL_DELETED, memoryCard.getBlockAt(6).getSaveType());
		assertEquals(SaveType.MIDDLE_LINK_DELETED, memoryCard.getBlockAt(7).getSaveType());
		assertEquals(SaveType.END_LINK_DELETED, memoryCard.getBlockAt(8).getSaveType());
		MemoryCardController.toggleSaveTypeDeleted(memoryCard, 7);
		System.out.println("After:");
		System.out.println(memoryCard);		
		assertNotNull(memoryCard);
		assertEquals(SaveType.INITIAL, memoryCard.getBlockAt(6).getSaveType());
		assertEquals(SaveType.MIDDLE_LINK, memoryCard.getBlockAt(7).getSaveType());
		assertEquals(SaveType.END_LINK, memoryCard.getBlockAt(8).getSaveType());
	}
	
	@Test
	public void testToggleDeletedBlockWheEndLinkedThenLinkedDeleted() throws IOException {
    	File file = new File(getClass().getClassLoader().getResource("Memorycard3.mcr").getFile());
		MemoryCard memoryCard = MemoryCardController.getInstance(file);			
		System.out.println("Before:");
		System.out.println(memoryCard);			               
		assertNotNull(memoryCard);
		assertNotNull(memoryCard.getBlocks());
		assertEquals(SaveType.INITIAL, memoryCard.getBlockAt(6).getSaveType());
		assertEquals(SaveType.MIDDLE_LINK, memoryCard.getBlockAt(7).getSaveType());
		assertEquals(SaveType.END_LINK, memoryCard.getBlockAt(8).getSaveType());
		MemoryCardController.toggleSaveTypeDeleted(memoryCard, 8);
		System.out.println("After:");
		System.out.println(memoryCard);		
		assertNotNull(memoryCard);
		assertEquals(SaveType.INITIAL_DELETED, memoryCard.getBlockAt(6).getSaveType());
		assertEquals(SaveType.MIDDLE_LINK_DELETED, memoryCard.getBlockAt(7).getSaveType());
		assertEquals(SaveType.END_LINK_DELETED, memoryCard.getBlockAt(8).getSaveType());
	}
	
	@Test
	public void testToggleDeletedBlockWheEndLinkedDeletedThenLinkedNotDeleted() throws IOException {
    	File file = new File(getClass().getClassLoader().getResource("Memorycard3_deleted.mcr").getFile());
		MemoryCard memoryCard = MemoryCardController.getInstance(file);			
		System.out.println("Before:");
		System.out.println(memoryCard);			               
		assertNotNull(memoryCard);
		assertNotNull(memoryCard.getBlocks());
		assertEquals(SaveType.INITIAL_DELETED, memoryCard.getBlockAt(6).getSaveType());
		assertEquals(SaveType.MIDDLE_LINK_DELETED, memoryCard.getBlockAt(7).getSaveType());
		assertEquals(SaveType.END_LINK_DELETED, memoryCard.getBlockAt(8).getSaveType());
		MemoryCardController.toggleSaveTypeDeleted(memoryCard, 8);
		System.out.println("After:");
		System.out.println(memoryCard);		
		assertNotNull(memoryCard);
		assertEquals(SaveType.INITIAL, memoryCard.getBlockAt(6).getSaveType());
		assertEquals(SaveType.MIDDLE_LINK, memoryCard.getBlockAt(7).getSaveType());
		assertEquals(SaveType.END_LINK, memoryCard.getBlockAt(8).getSaveType());
	}
	
	@Test
	public void testToggleDeletedBlockWheFormattedThenDoNothing() throws IOException {
    	File file = new File(getClass().getClassLoader().getResource("Memorycard3.mcr").getFile());
		MemoryCard memoryCard = MemoryCardController.getInstance(file);			
		System.out.println("Before:");
		System.out.println(memoryCard);			               
		assertNotNull(memoryCard);
		assertNotNull(memoryCard.getBlocks());
		assertEquals(SaveType.FORMATTED, memoryCard.getBlockAt(10).getSaveType());
		MemoryCardController.toggleSaveTypeDeleted(memoryCard, 10);
		System.out.println("After:");
		System.out.println(memoryCard);		
		assertNotNull(memoryCard);
		assertEquals(SaveType.FORMATTED, memoryCard.getBlockAt(10).getSaveType());		
	}
	
	@Test
	public void testFormatBlockWhenNoLinkedThenSingleFormatted() throws IOException {
    	File file = new File(getClass().getClassLoader().getResource("Memorycard3.mcr").getFile());
		MemoryCard memoryCard = MemoryCardController.getInstance(file);			
		System.out.println("Before:");
		System.out.println(memoryCard);			               
		assertNotNull(memoryCard);
		assertNotNull(memoryCard.getBlocks());
		assertEquals(SaveType.INITIAL, memoryCard.getBlockAt(0).getSaveType());
		assertEquals("SLUS-00594", memoryCard.getBlockAt(0).getProductCode());
		MemoryCardController.format(memoryCard, 0);
		System.out.println("After:");
		System.out.println(memoryCard);		
		assertNotNull(memoryCard);
		assertNotNull(memoryCard.getBlocks());
		assertEquals(SaveType.FORMATTED, memoryCard.getBlockAt(0).getSaveType());
		assertTrue(memoryCard.getBlockAt(0).getProductCode().trim().isEmpty());
	}
	
	@Test
	public void testFormatBlockWhenInitialLinkedThenLinkedFormatted() throws IOException {
    	File file = new File(getClass().getClassLoader().getResource("Memorycard3.mcr").getFile());
		MemoryCard memoryCard = MemoryCardController.getInstance(file);			
		System.out.println("Before:");
		System.out.println(memoryCard);			               
		assertNotNull(memoryCard);
		assertNotNull(memoryCard.getBlocks());
		assertEquals(SaveType.INITIAL, memoryCard.getBlockAt(1).getSaveType());
		assertEquals(SaveType.END_LINK, memoryCard.getBlockAt(2).getSaveType());
		assertEquals("SLUS-00292", memoryCard.getBlockAt(1).getProductCode());		
		MemoryCardController.format(memoryCard, 1);
		System.out.println("After:");
		System.out.println(memoryCard);		
		assertNotNull(memoryCard);
		assertNotNull(memoryCard.getBlocks());
		assertEquals(SaveType.FORMATTED, memoryCard.getBlockAt(1).getSaveType());
		assertEquals(SaveType.FORMATTED, memoryCard.getBlockAt(2).getSaveType());
		assertTrue(memoryCard.getBlockAt(1).getProductCode().trim().isEmpty());
	}
	
	@Test
	public void testFormatBlockWhenMiddleLinkedThenLinkedFormatted() throws IOException {
    	File file = new File(getClass().getClassLoader().getResource("Memorycard3.mcr").getFile());
		MemoryCard memoryCard = MemoryCardController.getInstance(file);			
		System.out.println("Before:");
		System.out.println(memoryCard);			               
		assertNotNull(memoryCard);
		assertNotNull(memoryCard.getBlocks());
		assertEquals(SaveType.INITIAL, memoryCard.getBlockAt(6).getSaveType());
		assertEquals(SaveType.MIDDLE_LINK, memoryCard.getBlockAt(7).getSaveType());
		assertEquals(SaveType.END_LINK, memoryCard.getBlockAt(8).getSaveType());
		assertEquals("SLUS-00614", memoryCard.getBlockAt(6).getProductCode());		
		MemoryCardController.format(memoryCard, 7);
		System.out.println("After:");
		System.out.println(memoryCard);		
		assertNotNull(memoryCard);
		assertNotNull(memoryCard.getBlocks());
		assertEquals(SaveType.FORMATTED, memoryCard.getBlockAt(6).getSaveType());
		assertEquals(SaveType.FORMATTED, memoryCard.getBlockAt(7).getSaveType());
		assertEquals(SaveType.FORMATTED, memoryCard.getBlockAt(8).getSaveType());
		assertTrue(memoryCard.getBlockAt(6).getProductCode().trim().isEmpty());
	}
	
	@Test
	public void testFormatBlockWhenEndLinkedThenLinkedFormatted() throws IOException {
    	File file = new File(getClass().getClassLoader().getResource("Memorycard3.mcr").getFile());
		MemoryCard memoryCard = MemoryCardController.getInstance(file);			
		System.out.println("Before:");
		System.out.println(memoryCard);			               
		assertNotNull(memoryCard);
		assertNotNull(memoryCard.getBlocks());
		assertEquals(SaveType.INITIAL, memoryCard.getBlockAt(6).getSaveType());
		assertEquals(SaveType.MIDDLE_LINK, memoryCard.getBlockAt(7).getSaveType());
		assertEquals(SaveType.END_LINK, memoryCard.getBlockAt(8).getSaveType());
		assertEquals("SLUS-00614", memoryCard.getBlockAt(6).getProductCode());		
		MemoryCardController.format(memoryCard, 8);
		System.out.println("After:");
		System.out.println(memoryCard);		
		assertNotNull(memoryCard);
		assertNotNull(memoryCard.getBlocks());
		assertEquals(SaveType.FORMATTED, memoryCard.getBlockAt(6).getSaveType());
		assertEquals(SaveType.FORMATTED, memoryCard.getBlockAt(7).getSaveType());
		assertEquals(SaveType.FORMATTED, memoryCard.getBlockAt(8).getSaveType());
		assertTrue(memoryCard.getBlockAt(6).getProductCode().trim().isEmpty());
	}
	
	@Test
	public void testFormatAllMemoryCard() throws IOException {
    	File file = new File(getClass().getClassLoader().getResource("Memorycard3.mcr").getFile());
		MemoryCard memoryCard = MemoryCardController.getInstance(file);			
		System.out.println("Before:");
		System.out.println(memoryCard);			               
		assertNotNull(memoryCard);
		assertNotNull(memoryCard.getBlocks());
		assertEquals(SaveType.INITIAL, memoryCard.getBlockAt(6).getSaveType());
		assertEquals(SaveType.MIDDLE_LINK, memoryCard.getBlockAt(7).getSaveType());
		assertEquals(SaveType.END_LINK, memoryCard.getBlockAt(8).getSaveType());
		assertEquals("SLUS-00614", memoryCard.getBlockAt(6).getProductCode());		
		MemoryCardController.format(memoryCard);
		System.out.println("After:");
		System.out.println(memoryCard);		
		assertNotNull(memoryCard);
		assertNotNull(memoryCard.getBlocks());
		assertEquals(SaveType.FORMATTED, memoryCard.getBlockAt(6).getSaveType());
		assertEquals(SaveType.FORMATTED, memoryCard.getBlockAt(7).getSaveType());
		assertEquals(SaveType.FORMATTED, memoryCard.getBlockAt(8).getSaveType());
		assertTrue(memoryCard.getBlockAt(6).getProductCode().trim().isEmpty());
	}
}
