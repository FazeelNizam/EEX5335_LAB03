import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class MemoryManagementSimulator {
    // Constants
    private static final int TLB_SIZE = 16;
    private static final int PAGE_TABLE_SIZE = 256;
    private static final int CACHE_SIZE = 64;
    private static final int MEMORY_SIZE = 1024;

    // Data Structures
    private Map<Integer, Integer> tlb;
    private Map<Integer, Integer> pageTable;
    private Map<Integer, Integer> cache;
    private int[] memory;

    // Constructor to initialize data structures
    public MemoryManagementSimulator() {
        // Initialize TLB with LRU policy using LinkedHashMap
        tlb = new LinkedHashMap<Integer, Integer>(TLB_SIZE, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<Integer, Integer> eldest) {
                return size() > TLB_SIZE;
            }
        };

        // Initialize Page Table
        pageTable = new HashMap<>();

        // Initialize Cache with LRU policy using LinkedHashMap
        cache = new LinkedHashMap<Integer, Integer>(CACHE_SIZE, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<Integer, Integer> eldest) {
                return size() > CACHE_SIZE;
            }
        };

        // Initialize Main Memory
        memory = new int[MEMORY_SIZE];
        for (int i = 0; i < MEMORY_SIZE; i++) {
            memory[i] = i;
        }
    }

    // Simulate memory access
    public int accessMemory(int virtualAddress) {
        // Check TLB
        if (tlb.containsKey(virtualAddress)) {
            System.out.println("TLB Hit");
            int physicalAddress = tlb.get(virtualAddress);
            return accessCache(physicalAddress);
        } else {
            System.out.println("TLB Miss");
            return accessPageTable(virtualAddress);
        }
    }

    // Check Page Table
    private int accessPageTable(int virtualAddress) {
        if (pageTable.containsKey(virtualAddress)) {
            System.out.println("Page Table Hit");
            int physicalAddress = pageTable.get(virtualAddress);
            tlb.put(virtualAddress, physicalAddress); // Update TLB
            return accessCache(physicalAddress);
        } else {
            System.out.println("Page Table Miss - Page Fault");
            return handlePageFault(virtualAddress);
        }
    }

    // Handle Page Fault
    private int handlePageFault(int virtualAddress) {
        int physicalAddress = virtualAddress % MEMORY_SIZE; // Simulate loading page into memory
        pageTable.put(virtualAddress, physicalAddress); // Update Page Table
        tlb.put(virtualAddress, physicalAddress); // Update TLB
        return accessCache(physicalAddress);
    }

    // Check Cache
    private int accessCache(int physicalAddress) {
        if (cache.containsKey(physicalAddress)) {
            System.out.println("Cache Hit");
            return cache.get(physicalAddress);
        } else {
            System.out.println("Cache Miss");
            return accessMainMemory(physicalAddress);
        }
    }

    // Access Main Memory
    private int accessMainMemory(int physicalAddress) {
        int data = memory[physicalAddress];
        // Update Cache
        cache.put(physicalAddress, data);
        return data;
    }

    public static void main(String[] args) {
        MemoryManagementSimulator simulator = new MemoryManagementSimulator();
        int[] testAddresses = { 5, 20, 5, 300, 1023, 256, 1023, 20 };

        for (int address : testAddresses) {
            System.out.println("Accessing Virtual Address: " + address);
            int data = simulator.accessMemory(address);
            System.out.println("Data Retrieved: " + data);
            System.out.println("----------------------------------");
        }
    }
}
