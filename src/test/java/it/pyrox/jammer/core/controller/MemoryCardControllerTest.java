package it.pyrox.jammer.core.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import it.pyrox.jammer.core.enums.SaveTypeEnum;
import it.pyrox.jammer.core.exception.NotEnoughSpaceException;
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
	public void testFindLinkedBlocksWhenNoLinkedBlock() throws IOException {
    	File file = new File(getClass().getClassLoader().getResource("Memorycard3.mcr").getFile());
		MemoryCard memoryCard = MemoryCardController.getInstance(file);			
		System.out.println(memoryCard);			               
		assertNotNull(memoryCard);
		assertNotNull(memoryCard.getBlocks());
		assertEquals(SaveTypeEnum.INITIAL, memoryCard.getBlockAt(0).getSaveType());
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
		assertEquals(SaveTypeEnum.INITIAL, memoryCard.getBlockAt(1).getSaveType());
		assertEquals(SaveTypeEnum.END_LINK, memoryCard.getBlockAt(2).getSaveType());
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
		assertEquals(SaveTypeEnum.INITIAL, memoryCard.getBlockAt(6).getSaveType());
		assertEquals(SaveTypeEnum.MIDDLE_LINK, memoryCard.getBlockAt(7).getSaveType());
		assertEquals(SaveTypeEnum.END_LINK, memoryCard.getBlockAt(8).getSaveType());
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
		assertEquals(SaveTypeEnum.INITIAL, memoryCard.getBlockAt(6).getSaveType());
		assertEquals(SaveTypeEnum.MIDDLE_LINK, memoryCard.getBlockAt(7).getSaveType());
		assertEquals(SaveTypeEnum.END_LINK, memoryCard.getBlockAt(8).getSaveType());
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
		assertEquals(SaveTypeEnum.FORMATTED, memoryCard.getBlockAt(10).getSaveType());
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
		assertEquals(SaveTypeEnum.INITIAL, memoryCard.getBlockAt(0).getSaveType());
		MemoryCardController.toggleSaveTypeDeleted(memoryCard, 0);
		System.out.println("After:");
		System.out.println(memoryCard);		
		assertNotNull(memoryCard);
		assertNotNull(memoryCard.getBlocks());
		assertEquals(SaveTypeEnum.INITIAL_DELETED, memoryCard.getBlockAt(0).getSaveType());
	}
	
	@Test
	public void testToggleDeletedBlockWhenNoLinkedDeletedThenSingleInitial() throws IOException {
    	File file = new File(getClass().getClassLoader().getResource("Memorycard3_deleted.mcr").getFile());
		MemoryCard memoryCard = MemoryCardController.getInstance(file);			
		System.out.println("Before:");
		System.out.println(memoryCard);			               
		assertNotNull(memoryCard);
		assertNotNull(memoryCard.getBlocks());
		assertEquals(SaveTypeEnum.INITIAL_DELETED, memoryCard.getBlockAt(0).getSaveType());
		MemoryCardController.toggleSaveTypeDeleted(memoryCard, 0);
		System.out.println("After:");
		System.out.println(memoryCard);		
		assertNotNull(memoryCard);
		assertNotNull(memoryCard.getBlocks());
		assertEquals(SaveTypeEnum.INITIAL, memoryCard.getBlockAt(0).getSaveType());
	}
	
	@Test
	public void testToggleDeletedBlockWhenInitialLinkedThenLinkedDeleted() throws IOException {
    	File file = new File(getClass().getClassLoader().getResource("Memorycard3.mcr").getFile());
		MemoryCard memoryCard = MemoryCardController.getInstance(file);			
		System.out.println("Before:");
		System.out.println(memoryCard);			               
		assertNotNull(memoryCard);
		assertNotNull(memoryCard.getBlocks());
		assertEquals(SaveTypeEnum.INITIAL, memoryCard.getBlockAt(1).getSaveType());
		assertEquals(SaveTypeEnum.END_LINK, memoryCard.getBlockAt(2).getSaveType());
		MemoryCardController.toggleSaveTypeDeleted(memoryCard, 1);
		System.out.println("After:");
		System.out.println(memoryCard);		
		assertNotNull(memoryCard);
		assertNotNull(memoryCard.getBlocks());
		assertEquals(SaveTypeEnum.INITIAL_DELETED, memoryCard.getBlockAt(1).getSaveType());
		assertEquals(SaveTypeEnum.END_LINK_DELETED, memoryCard.getBlockAt(2).getSaveType());
	}
	
	@Test
	public void testToggleDeletedBlockWhenInitialLinkedDeletedThenLinkedNotDeleted() throws IOException {
    	File file = new File(getClass().getClassLoader().getResource("Memorycard3_deleted.mcr").getFile());
		MemoryCard memoryCard = MemoryCardController.getInstance(file);			
		System.out.println("Before:");
		System.out.println(memoryCard);			               
		assertNotNull(memoryCard);
		assertNotNull(memoryCard.getBlocks());
		assertEquals(SaveTypeEnum.INITIAL_DELETED, memoryCard.getBlockAt(1).getSaveType());
		assertEquals(SaveTypeEnum.END_LINK_DELETED, memoryCard.getBlockAt(2).getSaveType());
		MemoryCardController.toggleSaveTypeDeleted(memoryCard, 1);
		System.out.println("After:");
		System.out.println(memoryCard);		
		assertNotNull(memoryCard);
		assertNotNull(memoryCard.getBlocks());
		assertEquals(SaveTypeEnum.INITIAL, memoryCard.getBlockAt(1).getSaveType());
		assertEquals(SaveTypeEnum.END_LINK, memoryCard.getBlockAt(2).getSaveType());
	}
	
	@Test
	public void testToggleDeletedBlockWheMiddleLinkedThenLinkedDeleted() throws IOException {
    	File file = new File(getClass().getClassLoader().getResource("Memorycard3.mcr").getFile());
		MemoryCard memoryCard = MemoryCardController.getInstance(file);			
		System.out.println("Before:");
		System.out.println(memoryCard);			               
		assertNotNull(memoryCard);
		assertNotNull(memoryCard.getBlocks());
		assertEquals(SaveTypeEnum.INITIAL, memoryCard.getBlockAt(6).getSaveType());
		assertEquals(SaveTypeEnum.MIDDLE_LINK, memoryCard.getBlockAt(7).getSaveType());
		assertEquals(SaveTypeEnum.END_LINK, memoryCard.getBlockAt(8).getSaveType());
		MemoryCardController.toggleSaveTypeDeleted(memoryCard, 7);
		System.out.println("After:");
		System.out.println(memoryCard);		
		assertNotNull(memoryCard);
		assertEquals(SaveTypeEnum.INITIAL_DELETED, memoryCard.getBlockAt(6).getSaveType());
		assertEquals(SaveTypeEnum.MIDDLE_LINK_DELETED, memoryCard.getBlockAt(7).getSaveType());
		assertEquals(SaveTypeEnum.END_LINK_DELETED, memoryCard.getBlockAt(8).getSaveType());
	}
	
	@Test
	public void testToggleDeletedBlockWheMiddleLinkedDeletedThenLinkedNotDeleted() throws IOException {
    	File file = new File(getClass().getClassLoader().getResource("Memorycard3_deleted.mcr").getFile());
		MemoryCard memoryCard = MemoryCardController.getInstance(file);			
		System.out.println("Before:");
		System.out.println(memoryCard);			               
		assertNotNull(memoryCard);
		assertNotNull(memoryCard.getBlocks());
		assertEquals(SaveTypeEnum.INITIAL_DELETED, memoryCard.getBlockAt(6).getSaveType());
		assertEquals(SaveTypeEnum.MIDDLE_LINK_DELETED, memoryCard.getBlockAt(7).getSaveType());
		assertEquals(SaveTypeEnum.END_LINK_DELETED, memoryCard.getBlockAt(8).getSaveType());
		MemoryCardController.toggleSaveTypeDeleted(memoryCard, 7);
		System.out.println("After:");
		System.out.println(memoryCard);		
		assertNotNull(memoryCard);
		assertEquals(SaveTypeEnum.INITIAL, memoryCard.getBlockAt(6).getSaveType());
		assertEquals(SaveTypeEnum.MIDDLE_LINK, memoryCard.getBlockAt(7).getSaveType());
		assertEquals(SaveTypeEnum.END_LINK, memoryCard.getBlockAt(8).getSaveType());
	}
	
	@Test
	public void testToggleDeletedBlockWheEndLinkedThenLinkedDeleted() throws IOException {
    	File file = new File(getClass().getClassLoader().getResource("Memorycard3.mcr").getFile());
		MemoryCard memoryCard = MemoryCardController.getInstance(file);			
		System.out.println("Before:");
		System.out.println(memoryCard);			               
		assertNotNull(memoryCard);
		assertNotNull(memoryCard.getBlocks());
		assertEquals(SaveTypeEnum.INITIAL, memoryCard.getBlockAt(6).getSaveType());
		assertEquals(SaveTypeEnum.MIDDLE_LINK, memoryCard.getBlockAt(7).getSaveType());
		assertEquals(SaveTypeEnum.END_LINK, memoryCard.getBlockAt(8).getSaveType());
		MemoryCardController.toggleSaveTypeDeleted(memoryCard, 8);
		System.out.println("After:");
		System.out.println(memoryCard);		
		assertNotNull(memoryCard);
		assertEquals(SaveTypeEnum.INITIAL_DELETED, memoryCard.getBlockAt(6).getSaveType());
		assertEquals(SaveTypeEnum.MIDDLE_LINK_DELETED, memoryCard.getBlockAt(7).getSaveType());
		assertEquals(SaveTypeEnum.END_LINK_DELETED, memoryCard.getBlockAt(8).getSaveType());
	}
	
	@Test
	public void testToggleDeletedBlockWheEndLinkedDeletedThenLinkedNotDeleted() throws IOException {
    	File file = new File(getClass().getClassLoader().getResource("Memorycard3_deleted.mcr").getFile());
		MemoryCard memoryCard = MemoryCardController.getInstance(file);			
		System.out.println("Before:");
		System.out.println(memoryCard);			               
		assertNotNull(memoryCard);
		assertNotNull(memoryCard.getBlocks());
		assertEquals(SaveTypeEnum.INITIAL_DELETED, memoryCard.getBlockAt(6).getSaveType());
		assertEquals(SaveTypeEnum.MIDDLE_LINK_DELETED, memoryCard.getBlockAt(7).getSaveType());
		assertEquals(SaveTypeEnum.END_LINK_DELETED, memoryCard.getBlockAt(8).getSaveType());
		MemoryCardController.toggleSaveTypeDeleted(memoryCard, 8);
		System.out.println("After:");
		System.out.println(memoryCard);		
		assertNotNull(memoryCard);
		assertEquals(SaveTypeEnum.INITIAL, memoryCard.getBlockAt(6).getSaveType());
		assertEquals(SaveTypeEnum.MIDDLE_LINK, memoryCard.getBlockAt(7).getSaveType());
		assertEquals(SaveTypeEnum.END_LINK, memoryCard.getBlockAt(8).getSaveType());
	}
	
	@Test
	public void testToggleDeletedBlockWheFormattedThenDoNothing() throws IOException {
    	File file = new File(getClass().getClassLoader().getResource("Memorycard3.mcr").getFile());
		MemoryCard memoryCard = MemoryCardController.getInstance(file);			
		System.out.println("Before:");
		System.out.println(memoryCard);			               
		assertNotNull(memoryCard);
		assertNotNull(memoryCard.getBlocks());
		assertEquals(SaveTypeEnum.FORMATTED, memoryCard.getBlockAt(10).getSaveType());
		MemoryCardController.toggleSaveTypeDeleted(memoryCard, 10);
		System.out.println("After:");
		System.out.println(memoryCard);		
		assertNotNull(memoryCard);
		assertEquals(SaveTypeEnum.FORMATTED, memoryCard.getBlockAt(10).getSaveType());		
	}
	
	@Test
	public void testFormatBlockWhenNoLinkedThenSingleFormatted() throws IOException {
    	File file = new File(getClass().getClassLoader().getResource("Memorycard3.mcr").getFile());
		MemoryCard memoryCard = MemoryCardController.getInstance(file);			
		System.out.println("Before:");
		System.out.println(memoryCard);			               
		assertNotNull(memoryCard);
		assertNotNull(memoryCard.getBlocks());
		assertEquals(SaveTypeEnum.INITIAL, memoryCard.getBlockAt(0).getSaveType());
		assertEquals("SLUS-00594", memoryCard.getBlockAt(0).getProductCode());
		MemoryCardController.format(memoryCard, 0);
		System.out.println("After:");
		System.out.println(memoryCard);		
		assertNotNull(memoryCard);
		assertNotNull(memoryCard.getBlocks());
		assertEquals(SaveTypeEnum.FORMATTED, memoryCard.getBlockAt(0).getSaveType());
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
		assertEquals(SaveTypeEnum.INITIAL, memoryCard.getBlockAt(1).getSaveType());
		assertEquals(SaveTypeEnum.END_LINK, memoryCard.getBlockAt(2).getSaveType());
		assertEquals("SLUS-00292", memoryCard.getBlockAt(1).getProductCode());		
		MemoryCardController.format(memoryCard, 1);
		System.out.println("After:");
		System.out.println(memoryCard);		
		assertNotNull(memoryCard);
		assertNotNull(memoryCard.getBlocks());
		assertEquals(SaveTypeEnum.FORMATTED, memoryCard.getBlockAt(1).getSaveType());
		assertEquals(SaveTypeEnum.FORMATTED, memoryCard.getBlockAt(2).getSaveType());
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
		assertEquals(SaveTypeEnum.INITIAL, memoryCard.getBlockAt(6).getSaveType());
		assertEquals(SaveTypeEnum.MIDDLE_LINK, memoryCard.getBlockAt(7).getSaveType());
		assertEquals(SaveTypeEnum.END_LINK, memoryCard.getBlockAt(8).getSaveType());
		assertEquals("SLUS-00614", memoryCard.getBlockAt(6).getProductCode());		
		MemoryCardController.format(memoryCard, 7);
		System.out.println("After:");
		System.out.println(memoryCard);		
		assertNotNull(memoryCard);
		assertNotNull(memoryCard.getBlocks());
		assertEquals(SaveTypeEnum.FORMATTED, memoryCard.getBlockAt(6).getSaveType());
		assertEquals(SaveTypeEnum.FORMATTED, memoryCard.getBlockAt(7).getSaveType());
		assertEquals(SaveTypeEnum.FORMATTED, memoryCard.getBlockAt(8).getSaveType());
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
		assertEquals(SaveTypeEnum.INITIAL, memoryCard.getBlockAt(6).getSaveType());
		assertEquals(SaveTypeEnum.MIDDLE_LINK, memoryCard.getBlockAt(7).getSaveType());
		assertEquals(SaveTypeEnum.END_LINK, memoryCard.getBlockAt(8).getSaveType());
		assertEquals("SLUS-00614", memoryCard.getBlockAt(6).getProductCode());		
		MemoryCardController.format(memoryCard, 8);
		System.out.println("After:");
		System.out.println(memoryCard);		
		assertNotNull(memoryCard);
		assertNotNull(memoryCard.getBlocks());
		assertEquals(SaveTypeEnum.FORMATTED, memoryCard.getBlockAt(6).getSaveType());
		assertEquals(SaveTypeEnum.FORMATTED, memoryCard.getBlockAt(7).getSaveType());
		assertEquals(SaveTypeEnum.FORMATTED, memoryCard.getBlockAt(8).getSaveType());
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
		assertEquals(SaveTypeEnum.INITIAL, memoryCard.getBlockAt(6).getSaveType());
		assertEquals(SaveTypeEnum.MIDDLE_LINK, memoryCard.getBlockAt(7).getSaveType());
		assertEquals(SaveTypeEnum.END_LINK, memoryCard.getBlockAt(8).getSaveType());
		assertEquals("SLUS-00614", memoryCard.getBlockAt(6).getProductCode());		
		MemoryCardController.format(memoryCard);
		System.out.println("After:");
		System.out.println(memoryCard);		
		assertNotNull(memoryCard);
		assertNotNull(memoryCard.getBlocks());
		assertEquals(SaveTypeEnum.FORMATTED, memoryCard.getBlockAt(6).getSaveType());
		assertEquals(SaveTypeEnum.FORMATTED, memoryCard.getBlockAt(7).getSaveType());
		assertEquals(SaveTypeEnum.FORMATTED, memoryCard.getBlockAt(8).getSaveType());
		assertTrue(memoryCard.getBlockAt(6).getProductCode().trim().isEmpty());
	}
	
	@Test
	public void testSaveMemoryCardToFileAfterDelete() throws IOException {
		File inputFile = new File(getClass().getClassLoader().getResource("Memorycard3.mcr").getFile());
		MemoryCard memoryCard = MemoryCardController.getInstance(inputFile);			
		System.out.println("Before:");
		System.out.println(memoryCard);			               
		assertNotNull(memoryCard);
		assertNotNull(memoryCard.getBlocks());
		assertEquals(SaveTypeEnum.INITIAL, memoryCard.getBlockAt(6).getSaveType());
		assertEquals(SaveTypeEnum.MIDDLE_LINK, memoryCard.getBlockAt(7).getSaveType());
		assertEquals(SaveTypeEnum.END_LINK, memoryCard.getBlockAt(8).getSaveType());
		MemoryCardController.toggleSaveTypeDeleted(memoryCard, 8);
		MemoryCardController.saveInstance(memoryCard, new File(inputFile.getParent(), "Memorycard3_modified.mcr"));
		File inputFileModified = new File(getClass().getClassLoader().getResource("Memorycard3_modified.mcr").getFile());
		MemoryCard memoryCardModified = MemoryCardController.getInstance(inputFileModified);					
		System.out.println("After:");
		System.out.println(memoryCardModified);		
		assertNotNull(memoryCardModified);
		assertEquals(SaveTypeEnum.INITIAL_DELETED, memoryCardModified.getBlockAt(6).getSaveType());
		assertEquals(SaveTypeEnum.MIDDLE_LINK_DELETED, memoryCardModified.getBlockAt(7).getSaveType());
		assertEquals(SaveTypeEnum.END_LINK_DELETED, memoryCardModified.getBlockAt(8).getSaveType());
		inputFileModified.delete();
	}
	
	@Test
	public void testDefragMemoryCard() throws IOException {
		File inputFile = new File(getClass().getClassLoader().getResource("Memorycard3_defrag.mcr").getFile());
		MemoryCard memoryCard = MemoryCardController.getInstance(inputFile);			
		System.out.println("Before:");
		System.out.println(memoryCard);			               
		assertNotNull(memoryCard);
		assertNotNull(memoryCard.getBlocks());
		assertEquals(SaveTypeEnum.INITIAL, memoryCard.getBlockAt(0).getSaveType());
		assertEquals(0xFF, memoryCard.getBlockAt(0).getNextLinkIndex());
		assertEquals(SaveTypeEnum.FORMATTED, memoryCard.getBlockAt(1).getSaveType());
		assertEquals(0xFF, memoryCard.getBlockAt(1).getNextLinkIndex());
		assertEquals(SaveTypeEnum.FORMATTED, memoryCard.getBlockAt(2).getSaveType());
		assertEquals(0xFF, memoryCard.getBlockAt(2).getNextLinkIndex());
		assertEquals(SaveTypeEnum.INITIAL, memoryCard.getBlockAt(3).getSaveType());
		assertEquals(4, memoryCard.getBlockAt(3).getNextLinkIndex());
		assertEquals(SaveTypeEnum.END_LINK, memoryCard.getBlockAt(4).getSaveType());
		assertEquals(0xFF, memoryCard.getBlockAt(4).getNextLinkIndex());
		assertEquals(SaveTypeEnum.FORMATTED, memoryCard.getBlockAt(5).getSaveType());
		assertEquals(0xFF, memoryCard.getBlockAt(5).getNextLinkIndex());
		assertEquals(SaveTypeEnum.INITIAL, memoryCard.getBlockAt(6).getSaveType());
		assertEquals(7, memoryCard.getBlockAt(6).getNextLinkIndex());
		assertEquals(SaveTypeEnum.MIDDLE_LINK, memoryCard.getBlockAt(7).getSaveType());
		assertEquals(8, memoryCard.getBlockAt(7).getNextLinkIndex());
		assertEquals(SaveTypeEnum.END_LINK, memoryCard.getBlockAt(8).getSaveType());
		assertEquals(0xFF, memoryCard.getBlockAt(8).getNextLinkIndex());
		assertEquals(SaveTypeEnum.FORMATTED, memoryCard.getBlockAt(9).getSaveType());
		assertEquals(0xFF, memoryCard.getBlockAt(9).getNextLinkIndex());
		assertEquals(SaveTypeEnum.FORMATTED, memoryCard.getBlockAt(10).getSaveType());
		assertEquals(0xFF, memoryCard.getBlockAt(10).getNextLinkIndex());
		assertEquals(SaveTypeEnum.FORMATTED, memoryCard.getBlockAt(11).getSaveType());
		assertEquals(0xFF, memoryCard.getBlockAt(11).getNextLinkIndex());
		assertEquals(SaveTypeEnum.FORMATTED, memoryCard.getBlockAt(12).getSaveType());
		assertEquals(0xFF, memoryCard.getBlockAt(12).getNextLinkIndex());
		assertEquals(SaveTypeEnum.FORMATTED, memoryCard.getBlockAt(13).getSaveType());
		assertEquals(0xFF, memoryCard.getBlockAt(13).getNextLinkIndex());
		assertEquals(SaveTypeEnum.FORMATTED, memoryCard.getBlockAt(14).getSaveType());
		assertEquals(0xFF, memoryCard.getBlockAt(14).getNextLinkIndex());
		MemoryCardController.defrag(memoryCard);
		System.out.println("After:");
		System.out.println(memoryCard);
		assertNotNull(memoryCard);
		assertNotNull(memoryCard.getBlocks());
		assertEquals(SaveTypeEnum.INITIAL, memoryCard.getBlockAt(0).getSaveType());
		assertEquals(0xFF, memoryCard.getBlockAt(0).getNextLinkIndex());
		assertEquals(SaveTypeEnum.INITIAL, memoryCard.getBlockAt(1).getSaveType());
		assertEquals(2, memoryCard.getBlockAt(1).getNextLinkIndex());
		assertEquals(SaveTypeEnum.END_LINK, memoryCard.getBlockAt(2).getSaveType());
		assertEquals(0xFF, memoryCard.getBlockAt(2).getNextLinkIndex());
		assertEquals(SaveTypeEnum.INITIAL, memoryCard.getBlockAt(3).getSaveType());
		assertEquals(4, memoryCard.getBlockAt(3).getNextLinkIndex());
		assertEquals(SaveTypeEnum.MIDDLE_LINK, memoryCard.getBlockAt(4).getSaveType());
		assertEquals(5, memoryCard.getBlockAt(4).getNextLinkIndex());
		assertEquals(SaveTypeEnum.END_LINK, memoryCard.getBlockAt(5).getSaveType());
		assertEquals(0xFF, memoryCard.getBlockAt(5).getNextLinkIndex());
		assertEquals(SaveTypeEnum.FORMATTED, memoryCard.getBlockAt(6).getSaveType());
		assertEquals(0xFF, memoryCard.getBlockAt(6).getNextLinkIndex());
		assertEquals(SaveTypeEnum.FORMATTED, memoryCard.getBlockAt(7).getSaveType());
		assertEquals(0xFF, memoryCard.getBlockAt(7).getNextLinkIndex());
		assertEquals(SaveTypeEnum.FORMATTED, memoryCard.getBlockAt(8).getSaveType());
		assertEquals(0xFF, memoryCard.getBlockAt(8).getNextLinkIndex());
		assertEquals(SaveTypeEnum.FORMATTED, memoryCard.getBlockAt(9).getSaveType());
		assertEquals(0xFF, memoryCard.getBlockAt(9).getNextLinkIndex());
		assertEquals(SaveTypeEnum.FORMATTED, memoryCard.getBlockAt(10).getSaveType());
		assertEquals(0xFF, memoryCard.getBlockAt(10).getNextLinkIndex());
		assertEquals(SaveTypeEnum.FORMATTED, memoryCard.getBlockAt(11).getSaveType());
		assertEquals(0xFF, memoryCard.getBlockAt(11).getNextLinkIndex());
		assertEquals(SaveTypeEnum.FORMATTED, memoryCard.getBlockAt(12).getSaveType());
		assertEquals(0xFF, memoryCard.getBlockAt(12).getNextLinkIndex());
		assertEquals(SaveTypeEnum.FORMATTED, memoryCard.getBlockAt(13).getSaveType());
		assertEquals(0xFF, memoryCard.getBlockAt(13).getNextLinkIndex());
		assertEquals(SaveTypeEnum.FORMATTED, memoryCard.getBlockAt(14).getSaveType());
		assertEquals(0xFF, memoryCard.getBlockAt(14).getNextLinkIndex());
	}
	
	@Test
	public void testFindFirstEnoughContiguousEmptyBlocksWhenNeededIs2() throws IOException {
		File inputFile = new File(getClass().getClassLoader().getResource("Memorycard3_defrag.mcr").getFile());
		MemoryCard memoryCard = MemoryCardController.getInstance(inputFile);			
		System.out.println("Before:");
		System.out.println(memoryCard);			               
		assertNotNull(memoryCard);
		assertNotNull(memoryCard.getBlocks());
		assertEquals(SaveTypeEnum.INITIAL, memoryCard.getBlockAt(0).getSaveType());
		assertEquals(0xFF, memoryCard.getBlockAt(0).getNextLinkIndex());
		assertEquals(SaveTypeEnum.FORMATTED, memoryCard.getBlockAt(1).getSaveType());
		assertEquals(0xFF, memoryCard.getBlockAt(1).getNextLinkIndex());
		assertEquals(SaveTypeEnum.FORMATTED, memoryCard.getBlockAt(2).getSaveType());
		assertEquals(0xFF, memoryCard.getBlockAt(2).getNextLinkIndex());
		assertEquals(SaveTypeEnum.INITIAL, memoryCard.getBlockAt(3).getSaveType());
		assertEquals(4, memoryCard.getBlockAt(3).getNextLinkIndex());
		assertEquals(SaveTypeEnum.END_LINK, memoryCard.getBlockAt(4).getSaveType());
		assertEquals(0xFF, memoryCard.getBlockAt(4).getNextLinkIndex());
		assertEquals(SaveTypeEnum.FORMATTED, memoryCard.getBlockAt(5).getSaveType());
		assertEquals(0xFF, memoryCard.getBlockAt(5).getNextLinkIndex());
		assertEquals(SaveTypeEnum.INITIAL, memoryCard.getBlockAt(6).getSaveType());
		assertEquals(7, memoryCard.getBlockAt(6).getNextLinkIndex());
		assertEquals(SaveTypeEnum.MIDDLE_LINK, memoryCard.getBlockAt(7).getSaveType());
		assertEquals(8, memoryCard.getBlockAt(7).getNextLinkIndex());
		assertEquals(SaveTypeEnum.END_LINK, memoryCard.getBlockAt(8).getSaveType());
		assertEquals(0xFF, memoryCard.getBlockAt(8).getNextLinkIndex());
		assertEquals(SaveTypeEnum.FORMATTED, memoryCard.getBlockAt(9).getSaveType());
		assertEquals(0xFF, memoryCard.getBlockAt(9).getNextLinkIndex());
		assertEquals(SaveTypeEnum.FORMATTED, memoryCard.getBlockAt(10).getSaveType());
		assertEquals(0xFF, memoryCard.getBlockAt(10).getNextLinkIndex());
		assertEquals(SaveTypeEnum.FORMATTED, memoryCard.getBlockAt(11).getSaveType());
		assertEquals(0xFF, memoryCard.getBlockAt(11).getNextLinkIndex());
		assertEquals(SaveTypeEnum.FORMATTED, memoryCard.getBlockAt(12).getSaveType());
		assertEquals(0xFF, memoryCard.getBlockAt(12).getNextLinkIndex());
		assertEquals(SaveTypeEnum.FORMATTED, memoryCard.getBlockAt(13).getSaveType());
		assertEquals(0xFF, memoryCard.getBlockAt(13).getNextLinkIndex());
		assertEquals(SaveTypeEnum.FORMATTED, memoryCard.getBlockAt(14).getSaveType());
		assertEquals(0xFF, memoryCard.getBlockAt(14).getNextLinkIndex());
		int result = MemoryCardController.findFirstEnoughContiguousEmptyBlocks(memoryCard, 2);
		System.out.println("After:");
		System.out.println(memoryCard);
		assertNotNull(memoryCard);
		assertNotNull(memoryCard.getBlocks());
		assertEquals(1, result);
	}
	
	@Test
	public void testFindFirstEnoughContiguousEmptyBlocksWhenNeededIs3() throws IOException {
		File inputFile = new File(getClass().getClassLoader().getResource("Memorycard3_defrag.mcr").getFile());
		MemoryCard memoryCard = MemoryCardController.getInstance(inputFile);			
		System.out.println("Before:");
		System.out.println(memoryCard);			               
		assertNotNull(memoryCard);
		assertNotNull(memoryCard.getBlocks());
		assertEquals(SaveTypeEnum.INITIAL, memoryCard.getBlockAt(0).getSaveType());
		assertEquals(0xFF, memoryCard.getBlockAt(0).getNextLinkIndex());
		assertEquals(SaveTypeEnum.FORMATTED, memoryCard.getBlockAt(1).getSaveType());
		assertEquals(0xFF, memoryCard.getBlockAt(1).getNextLinkIndex());
		assertEquals(SaveTypeEnum.FORMATTED, memoryCard.getBlockAt(2).getSaveType());
		assertEquals(0xFF, memoryCard.getBlockAt(2).getNextLinkIndex());
		assertEquals(SaveTypeEnum.INITIAL, memoryCard.getBlockAt(3).getSaveType());
		assertEquals(4, memoryCard.getBlockAt(3).getNextLinkIndex());
		assertEquals(SaveTypeEnum.END_LINK, memoryCard.getBlockAt(4).getSaveType());
		assertEquals(0xFF, memoryCard.getBlockAt(4).getNextLinkIndex());
		assertEquals(SaveTypeEnum.FORMATTED, memoryCard.getBlockAt(5).getSaveType());
		assertEquals(0xFF, memoryCard.getBlockAt(5).getNextLinkIndex());
		assertEquals(SaveTypeEnum.INITIAL, memoryCard.getBlockAt(6).getSaveType());
		assertEquals(7, memoryCard.getBlockAt(6).getNextLinkIndex());
		assertEquals(SaveTypeEnum.MIDDLE_LINK, memoryCard.getBlockAt(7).getSaveType());
		assertEquals(8, memoryCard.getBlockAt(7).getNextLinkIndex());
		assertEquals(SaveTypeEnum.END_LINK, memoryCard.getBlockAt(8).getSaveType());
		assertEquals(0xFF, memoryCard.getBlockAt(8).getNextLinkIndex());
		assertEquals(SaveTypeEnum.FORMATTED, memoryCard.getBlockAt(9).getSaveType());
		assertEquals(0xFF, memoryCard.getBlockAt(9).getNextLinkIndex());
		assertEquals(SaveTypeEnum.FORMATTED, memoryCard.getBlockAt(10).getSaveType());
		assertEquals(0xFF, memoryCard.getBlockAt(10).getNextLinkIndex());
		assertEquals(SaveTypeEnum.FORMATTED, memoryCard.getBlockAt(11).getSaveType());
		assertEquals(0xFF, memoryCard.getBlockAt(11).getNextLinkIndex());
		assertEquals(SaveTypeEnum.FORMATTED, memoryCard.getBlockAt(12).getSaveType());
		assertEquals(0xFF, memoryCard.getBlockAt(12).getNextLinkIndex());
		assertEquals(SaveTypeEnum.FORMATTED, memoryCard.getBlockAt(13).getSaveType());
		assertEquals(0xFF, memoryCard.getBlockAt(13).getNextLinkIndex());
		assertEquals(SaveTypeEnum.FORMATTED, memoryCard.getBlockAt(14).getSaveType());
		assertEquals(0xFF, memoryCard.getBlockAt(14).getNextLinkIndex());
		int result = MemoryCardController.findFirstEnoughContiguousEmptyBlocks(memoryCard, 3);
		System.out.println("After:");
		System.out.println(memoryCard);
		assertNotNull(memoryCard);
		assertNotNull(memoryCard.getBlocks());
		assertEquals(9, result);
	}
	
	@Test
	public void testFindFirstEnoughContiguousEmptyBlocksWhenNeededIs10() throws IOException {
		File inputFile = new File(getClass().getClassLoader().getResource("Memorycard3_defrag.mcr").getFile());
		MemoryCard memoryCard = MemoryCardController.getInstance(inputFile);			
		System.out.println("Before:");
		System.out.println(memoryCard);			               
		assertNotNull(memoryCard);
		assertNotNull(memoryCard.getBlocks());
		assertEquals(SaveTypeEnum.INITIAL, memoryCard.getBlockAt(0).getSaveType());
		assertEquals(0xFF, memoryCard.getBlockAt(0).getNextLinkIndex());
		assertEquals(SaveTypeEnum.FORMATTED, memoryCard.getBlockAt(1).getSaveType());
		assertEquals(0xFF, memoryCard.getBlockAt(1).getNextLinkIndex());
		assertEquals(SaveTypeEnum.FORMATTED, memoryCard.getBlockAt(2).getSaveType());
		assertEquals(0xFF, memoryCard.getBlockAt(2).getNextLinkIndex());
		assertEquals(SaveTypeEnum.INITIAL, memoryCard.getBlockAt(3).getSaveType());
		assertEquals(4, memoryCard.getBlockAt(3).getNextLinkIndex());
		assertEquals(SaveTypeEnum.END_LINK, memoryCard.getBlockAt(4).getSaveType());
		assertEquals(0xFF, memoryCard.getBlockAt(4).getNextLinkIndex());
		assertEquals(SaveTypeEnum.FORMATTED, memoryCard.getBlockAt(5).getSaveType());
		assertEquals(0xFF, memoryCard.getBlockAt(5).getNextLinkIndex());
		assertEquals(SaveTypeEnum.INITIAL, memoryCard.getBlockAt(6).getSaveType());
		assertEquals(7, memoryCard.getBlockAt(6).getNextLinkIndex());
		assertEquals(SaveTypeEnum.MIDDLE_LINK, memoryCard.getBlockAt(7).getSaveType());
		assertEquals(8, memoryCard.getBlockAt(7).getNextLinkIndex());
		assertEquals(SaveTypeEnum.END_LINK, memoryCard.getBlockAt(8).getSaveType());
		assertEquals(0xFF, memoryCard.getBlockAt(8).getNextLinkIndex());
		assertEquals(SaveTypeEnum.FORMATTED, memoryCard.getBlockAt(9).getSaveType());
		assertEquals(0xFF, memoryCard.getBlockAt(9).getNextLinkIndex());
		assertEquals(SaveTypeEnum.FORMATTED, memoryCard.getBlockAt(10).getSaveType());
		assertEquals(0xFF, memoryCard.getBlockAt(10).getNextLinkIndex());
		assertEquals(SaveTypeEnum.FORMATTED, memoryCard.getBlockAt(11).getSaveType());
		assertEquals(0xFF, memoryCard.getBlockAt(11).getNextLinkIndex());
		assertEquals(SaveTypeEnum.FORMATTED, memoryCard.getBlockAt(12).getSaveType());
		assertEquals(0xFF, memoryCard.getBlockAt(12).getNextLinkIndex());
		assertEquals(SaveTypeEnum.FORMATTED, memoryCard.getBlockAt(13).getSaveType());
		assertEquals(0xFF, memoryCard.getBlockAt(13).getNextLinkIndex());
		assertEquals(SaveTypeEnum.FORMATTED, memoryCard.getBlockAt(14).getSaveType());
		assertEquals(0xFF, memoryCard.getBlockAt(14).getNextLinkIndex());
		int result = MemoryCardController.findFirstEnoughContiguousEmptyBlocks(memoryCard, 10);
		System.out.println("After:");
		System.out.println(memoryCard);
		assertNotNull(memoryCard);
		assertNotNull(memoryCard.getBlocks());
		assertEquals(-1, result);
	}
	
	@Test
	public void testMemoryCardDeepCopy() throws IOException {
		File inputFile = new File(getClass().getClassLoader().getResource("Memorycard3.mcr").getFile());
		MemoryCard memoryCard = MemoryCardController.getInstance(inputFile);			
		System.out.println("Before:");
		System.out.println(memoryCard);			               
		assertNotNull(memoryCard);
		assertNotNull(memoryCard.getBlocks());
		MemoryCard output = MemoryCardController.deepCopy(memoryCard);					
		System.out.println("After:");
		System.out.println(output);		
		assertNotNull(output);
		assertNotSame(memoryCard.getBlockAt(0), output.getBlockAt(0));
		// Test details only for the first block		
		assertDeepCopiedBlockIsOk(memoryCard.getBlockAt(0), output.getBlockAt(0));
		assertNotSame(memoryCard.getBlockAt(1), output.getBlockAt(1));
		assertNotSame(memoryCard.getBlockAt(2), output.getBlockAt(2));
		assertNotSame(memoryCard.getBlockAt(3), output.getBlockAt(3));
		assertNotSame(memoryCard.getBlockAt(4), output.getBlockAt(4));
		assertNotSame(memoryCard.getBlockAt(5), output.getBlockAt(5));
		assertNotSame(memoryCard.getBlockAt(6), output.getBlockAt(6));
		assertNotSame(memoryCard.getBlockAt(7), output.getBlockAt(7));
		assertNotSame(memoryCard.getBlockAt(8), output.getBlockAt(8));
		assertNotSame(memoryCard.getBlockAt(9), output.getBlockAt(9));
		assertNotSame(memoryCard.getBlockAt(10), output.getBlockAt(10));
		assertNotSame(memoryCard.getBlockAt(11), output.getBlockAt(11));
		assertNotSame(memoryCard.getBlockAt(12), output.getBlockAt(12));
		assertNotSame(memoryCard.getBlockAt(13), output.getBlockAt(13));
		assertNotSame(memoryCard.getBlockAt(14), output.getBlockAt(14));
	}
	
	@Test
	public void testCopyBlocksWithSingleSlot() throws IOException, NotEnoughSpaceException {
		File inputFileSource = new File(getClass().getClassLoader().getResource("Memorycard1.mcr").getFile());
		MemoryCard memoryCardSource = MemoryCardController.getInstance(inputFileSource);			
		System.out.println("Before MC1:");
		System.out.println(memoryCardSource);			               
		assertNotNull(memoryCardSource);
		assertNotNull(memoryCardSource.getBlocks());		
		File inputFileDest = new File(getClass().getClassLoader().getResource("Memorycard2.mcr").getFile());
		MemoryCard memoryCardDest = MemoryCardController.getInstance(inputFileDest);			
		System.out.println("Before MC2:");
		System.out.println(memoryCardDest);			               
		assertNotNull(memoryCardDest);
		assertNotNull(memoryCardDest.getBlocks());
		MemoryCardController.copyLinkedBlocks(memoryCardSource, memoryCardDest, 0);
		System.out.println("After MC2:");
		System.out.println(memoryCardDest);
		assertDeepCopiedBlockIsOkNoIndices(memoryCardSource.getBlockAt(0), memoryCardDest.getBlockAt(1));				
		assertEquals(1, memoryCardDest.getBlockAt(1).getIndex());		
		assertEquals(255, memoryCardDest.getBlockAt(1).getNextLinkIndex());
	}
	
	@Test
	public void testCopyBlocksWithMultipleSlots() throws IOException, NotEnoughSpaceException {
		File inputFileSource = new File(getClass().getClassLoader().getResource("Memorycard3.mcr").getFile());
		MemoryCard memoryCardSource = MemoryCardController.getInstance(inputFileSource);			
		System.out.println("Before MC1:");
		System.out.println(memoryCardSource);			               
		assertNotNull(memoryCardSource);
		assertNotNull(memoryCardSource.getBlocks());		
		File inputFileDest = new File(getClass().getClassLoader().getResource("Memorycard2.mcr").getFile());
		MemoryCard memoryCardDest = MemoryCardController.getInstance(inputFileDest);			
		System.out.println("Before MC2:");
		System.out.println(memoryCardDest);			               
		assertNotNull(memoryCardDest);
		assertNotNull(memoryCardDest.getBlocks());
		MemoryCardController.copyLinkedBlocks(memoryCardSource, memoryCardDest, 1);
		System.out.println("After MC2:");
		System.out.println(memoryCardDest);
		assertDeepCopiedBlockIsOkNoIndices(memoryCardSource.getBlockAt(1), memoryCardDest.getBlockAt(1));				
		assertEquals(1, memoryCardDest.getBlockAt(1).getIndex());		
		assertEquals(2, memoryCardDest.getBlockAt(1).getNextLinkIndex());
		assertDeepCopiedBlockIsOkNoIndices(memoryCardSource.getBlockAt(2), memoryCardDest.getBlockAt(2));			
		assertEquals(2, memoryCardDest.getBlockAt(2).getIndex());		
		assertEquals(255, memoryCardDest.getBlockAt(2).getNextLinkIndex());
	}
	
	@Test
	public void testCopyBlocksWithSingleSlotAndDestinationMemoryCardFormatted() throws IOException, NotEnoughSpaceException {
		File inputFileSource = new File(getClass().getClassLoader().getResource("Memorycard1.mcr").getFile());
		MemoryCard memoryCardSource = MemoryCardController.getInstance(inputFileSource);			
		System.out.println("Before MC1:");
		System.out.println(memoryCardSource);			               
		assertNotNull(memoryCardSource);
		assertNotNull(memoryCardSource.getBlocks());		
		File inputFileDest = new File(getClass().getClassLoader().getResource("Memorycard_formatted.mcr").getFile());
		MemoryCard memoryCardDest = MemoryCardController.getInstance(inputFileDest);			
		System.out.println("Before MC2:");
		System.out.println(memoryCardDest);			               
		assertNotNull(memoryCardDest);
		assertNotNull(memoryCardDest.getBlocks());
		MemoryCardController.copyLinkedBlocks(memoryCardSource, memoryCardDest, 0);
		System.out.println("After MC2:");
		System.out.println(memoryCardDest);
		assertDeepCopiedBlockIsOkNoIndices(memoryCardSource.getBlockAt(0), memoryCardDest.getBlockAt(0));				
		assertEquals(1, memoryCardDest.getBlockAt(1).getIndex());		
		assertEquals(255, memoryCardDest.getBlockAt(1).getNextLinkIndex());		
	}
	
	@Test
	public void testCopyAllBlocksIfNoEmptyTargetCardThenException() throws IOException {
		File inputFileSource = new File(getClass().getClassLoader().getResource("Memorycard3.mcr").getFile());
		MemoryCard memoryCardSource = MemoryCardController.getInstance(inputFileSource);		
		System.out.println("Before MC1:");
		System.out.println(memoryCardSource);
		assertNotNull(memoryCardSource);
		assertNotNull(memoryCardSource.getBlocks());
		File inputFileDest = new File(getClass().getClassLoader().getResource("Memorycard2.mcr").getFile());
		MemoryCard memoryCardDest = MemoryCardController.getInstance(inputFileDest);					
		System.out.println("Before MC2:");
		System.out.println(memoryCardDest);			               
		assertNotNull(memoryCardDest);
		assertNotNull(memoryCardDest.getBlocks());
		assertEquals(SaveTypeEnum.INITIAL, memoryCardDest.getBlockAt(0).getSaveType());
		assertThrows(NotEnoughSpaceException.class, () -> {
			MemoryCardController.copyAllBlocks(memoryCardSource, memoryCardDest);
		});
	}
	
	@Test
	public void testCopyAllBlocksIfEmptyTargetCardThenOk() throws IOException, NotEnoughSpaceException {
		File inputFileSource = new File(getClass().getClassLoader().getResource("Memorycard3.mcr").getFile());
		MemoryCard memoryCardSource = MemoryCardController.getInstance(inputFileSource);		
		System.out.println("Before MC1:");
		System.out.println(memoryCardSource);
		assertNotNull(memoryCardSource);
		assertNotNull(memoryCardSource.getBlocks());
		File inputFileDest = new File(getClass().getClassLoader().getResource("Memorycard_formatted.mcr").getFile());
		MemoryCard memoryCardDest = MemoryCardController.getInstance(inputFileDest);					
		System.out.println("Before MC2:");
		System.out.println(memoryCardDest);			               
		assertNotNull(memoryCardDest);
		assertNotNull(memoryCardDest.getBlocks());
		for (int i = 0; i < memoryCardDest.getBlocks().length; i++) {
			assertEquals(SaveTypeEnum.FORMATTED, memoryCardDest.getBlockAt(10).getSaveType());
		}
		MemoryCardController.copyAllBlocks(memoryCardSource, memoryCardDest);
		System.out.println("After MC2:");
		System.out.println(memoryCardDest);			               
		assertNotNull(memoryCardDest);
		assertNotNull(memoryCardDest.getBlocks());
		for (int i = 0; i < memoryCardDest.getBlocks().length; i++) {
			assertDeepCopiedBlockIsOk(memoryCardSource.getBlockAt(i), memoryCardDest.getBlockAt(i));
		}
	}
	
	private void assertDeepCopiedBlockIsOkNoIndices(Block source, Block dest) {			
		assertEquals(source.getCountryCode(), dest.getCountryCode());
		assertNotSame(source.getProductCode(), dest.getProductCode());
		assertEquals(source.getProductCode(), dest.getProductCode());
		assertNotSame(source.getIdentifier(), dest.getIdentifier());
		assertEquals(source.getIdentifier(), dest.getIdentifier());
		assertNotSame(source.getTitle(), dest.getTitle());
		assertEquals(source.getTitle(), dest.getTitle());		
		assertEquals(source.getSaveType(), dest.getSaveType());		
		assertEquals(source.getSaveSize(), dest.getSaveSize());		
		assertEquals(source.getNumFrames(), dest.getNumFrames());
		assertNotSame(source.getColorPalette(), dest.getColorPalette());
		assertEquals(true, Arrays.equals(source.getColorPalette(), dest.getColorPalette()));
		for (int i = 0; i < source.getColorPalette().length; i++) {
			assertNotSame(source.getColorPalette()[i], dest.getColorPalette()[i]);
		}
		assertNotSame(source.getIcons(), dest.getIcons());
		// BufferedImage doesn't implement equals(), so this will always be false, should implement a custom method
//		assertEquals(true, Arrays.equals(source.getIcons(), dest.getIcons()));
	}
	
	private void assertDeepCopiedBlockIsOk(Block source, Block dest) {
		assertDeepCopiedBlockIsOkNoIndices(source, dest);
		assertEquals(source.getRawIndex(), dest.getRawIndex());		
		assertEquals(source.getIndex(), dest.getIndex());		
		assertEquals(source.getNextLinkIndex(), dest.getNextLinkIndex());	
	}
}
