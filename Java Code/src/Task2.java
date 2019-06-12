import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

/**
 * To create Apache Lucene index in a folder and add files into this index based
 * on the input of the user.
 */
public class Task2 {
    private static Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_47);

    private IndexWriter writer;
    private ArrayList<File> queue = new ArrayList<File>();

    public static void main(String[] args) throws IOException {
	System.out
		.println("Enter the FULL path where the index will be created: (e.g. /Usr/index or c:\\temp\\index)");

	String indexLocation = null;
	BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
	String s = br.readLine();

	Task2 indexer = null;
	try {
	    indexLocation = s;
	    indexer = new Task2(s);
	} catch (Exception ex) {
	    System.out.println("Cannot create index..." + ex.getMessage());
	    System.exit(-1);
	}

	// ===================================================
	// read input from user until he enters q for quit
	// ===================================================
	while (!s.equalsIgnoreCase("q")) {
	    try {
		System.out
			.println("Enter the FULL path to add into the index (q=quit): (e.g. /home/mydir/docs or c:\\Users\\mydir\\docs)");
		System.out
			.println("[Acceptable file types: .xml, .html, .html, .txt]");
		s = br.readLine();
		if (s.equalsIgnoreCase("q")) {
		    break;
		}

		// try to add file into the index
		indexer.indexFileOrDirectory(s);
	    } catch (Exception e) {
		System.out.println("Error indexing " + s + " : "
			+ e.getMessage());
	    }
	}

	// ===================================================
	// after adding, we always have to call the
	// closeIndex, otherwise the index is not created
	// ===================================================
	indexer.closeIndex();

	// =========================================================
	// Now search
	// =========================================================
	String[] a = new String[] {"what articles exist which deal with tss time sharing system an operating system for ibm computers",
			 "i am interested in articles written either by prieve or udo pooch prieve b pooch u",
			 "intermediate languages used in construction of multi-targeted compilers tcoll",
			 "im interested in mechanisms for communicating between disjoint processes possibly but not exclusively in a distributed environment i would rather see descriptions of complete mechanisms with or without implementations as opposed to theoretical work on the abstract problem remote procedure calls and message-passing are examples of my interests",
			 "id like papers on design and implementation of editing interfaces window-managers command interpreters etc the essential issues are human interface design with views on improvements to user efficiency effectiveness and satisfaction",
			 "interested in articles on robotics motion planning particularly the geometric and combinatorial aspects we are not interested in the dynamics of arm motion",
			 "i am interested in distributed algorithms concurrent programs in which processes communicate and synchronize by using message passing areas of particular interest include fault-tolerance and techniques for understanding the correctness of these algorithms",
			 "addressing schemes for resources in networks resource addressing in network operating systems",
			 "security considerations in local networks network operating systems and distributed systems",
			 "parallel languages languages for parallel computation",
			 "setl very high level languages",
			 "portable operating systems",
			 "code optimization for space efficiency",
			 "find all discussions of optimal implementations of sort algorithms for database management applications",
			 "find all discussions of horizontal microcode optimization with special emphasis on optimization of loops and global optimization",
			 "find all descriptions of file handling in operating systems based on multiple processes and message passing",
			 "optimization of intermediate and machine code",
			 "languages and compilers for parallel processors especially highly horizontal microcoded machines code compaction",
			 "parallel algorithms",
			 "graph theoretic algorithms applicable to sparse matrices",
			 "computational complexity intractability class-complete reductions algorithms and efficiency",
			 "i am interested in hidden-line and hidden-surface algorithms for cylinders toroids spheres and cones this is a rather specialized topic in computer graphics",
			 "distributed computing structures and algorithms",
			 "applied stochastic processes",
			 "performance evaluation and modelling of computer systems",
			 "concurrency control mechanisms in operating systems",
			 "memory management aspects of operating systems",
			 "any information on packet radio networks of particular interest are algorithms for packet routing and for dealing with changes in network topography i am not interested in the hardware used in the network",
			 "number-theoretic algorithms especially involving prime number series sieves and chinese remainder theorem",
			 "articles on text formatting systems including what you see is what you get systems examples tnroff scribe bravo",
			 "id like to find articles describing the use of singular value decomposition in digital image processing applications include finding approximations to the original image and restoring images that are subject to noise an article on the subject is h andrews and c patterson outer product expansions and their uses in digital image processing american mathematical andrews h patterson c",
			 "id like to find articles describing graph algorithms that are based on the eigenvalue decomposition or singular value decomposition of the ajacency matrix for the graph im especially interested in any heuristic algorithms for graph coloring and graph isomorphism using this method",
			 "articles about the sensitivity of the eigenvalue decomposition of real matrices in particular zero-one matrices im especially interested in the separation of eigenspaces corresponding to distinct eigenvalues articles on the subject c davis and w kahn the rotation of eigenvectors by a permutation siam j numerical analysis vol 7 no 1 1970 g stewart error bounds for approximate invariant subspaces of closed linear operators siam j numerical analysis vol 8 no 4 1971 davis c kahn w stewart g",
			 "currently interested in isolation of root of polynomial there is an old more recent material heindel l",
			 "probabilistic algorithms especially those dealing with algebraic and symbolic manipulation some examples rabiin probabilistic algorithm on finite field siam waztch probabilistic testing of polynomial identities siam rabinm",
			 "fast algorithm for context-free language recognition or parsing",
			 "articles describing the relationship between data types and concurrency eg what is the type of a process when is a synchronization attempt between two processes type correct in a message-passing system is there any notion of the types of messages ie any way to check that the sender of the message and the receiver are both treating the bit stream as some particular type",
			 "what is the type of a module i dont want the entire literature on abstract data types here but im not sure how to phrase this to avoid it im interested in questions about how one can check that a module matches contexts in which it is used",
			 "what does type compatibility mean in languages that allow programmer defined types you might want to restrict this to extensible languages that allow definition of abstract data types or programmer-supplied definitions of operators like",
			 "list all articles dealing with data types in the following languages that are referenced frequently in papers on the above languages eg catch any languages with interesting type structures that i might have missed",
			 "theory of distributed systems and databases subtopics of special interest include reliability and fault-tolerance in distributed systems atomicity distributed transactions synchronization algorithms resource allocation lower bounds and models for asynchronous parallel systems also theory of communicating processes and protocols p box 2158 yale station new haven conn 06520",
			 "computer performance evaluation techniques using pattern recognition and clustering la 70803",
			 "analysis and perception of shape by humans and computers shape descriptions shape recognition by computer two-dimensional shapes measures of circularity shape matching",
			 "texture analysis by computer digitized texture analysis texture synthesis perception of texture",
			 "the use of operations research models to optimize information system performance this includes fine tuning decisions such as secondary index selection file reorganization and distributed databases",
			 "the application of fuzzy subset theory to clustering and information retrieval problems this includes performance evaluation and automatic indexing considerations",
			 "the use of bayesian decision models to optimize information retrieval system performance this includes stopping rules to determine when a user should cease scanning the output of a retrieval search",
			 "the use of computer science principles eg data structures numerical methods in generating optimization eg linear programming algorithms this includes issues of the khachian russian ellipsoidal algorithm and complexity of such algorithms",
			 "the role of information retrieval in knowledge based systems ie expert systems",
			 "parallel processors in information retrieval",
			 "parallel processors and paging algorithms",
			 "modelling and simulation in agricultural ecosystems",
			 "mathematical induction group theory integers modulo m probability binomial coefficients binomial theorem homomorphism morphism transitivity relations relation matrix syracuse university 313 link hall syracuse n 13210",
			 "semantics of programming languages including abstract specifications of data types denotational semantics and proofs of correctness hoare a dijkstra e university of massachusetts amherst ma 01003",
			 "anything dealing with star height of regular languages or regular expressions or regular events",
			 "articles relation the algebraic theory of semigroups and monoids to the study of automata and regular languages",
			 "abstracts of articles j backus can programming be liberated from the von neumann style a functional style and its algebra of programs cacm 21 re millo r lipton a perlis letter to acm forum cacm 22 1979 629-630 backus j de millo r lipton r perlis a",
			 "algorithms or statistical packages for anova regression using least squares or generalized linear models system design capabilities statistical formula are of interest students t test wilcoxon and sign tests multivariate and univariate components can be included",
			 "dictionary construction and accessing methods for fast retrieval of words or lexical items or morphologically related information hashing or indexing methods are usually applied to english spelling or natural language problems",
			 "hardware and software relating to database management systems database packages back end computers special associative hardware with microcomputers attached to disk heads or things like rap relational or network codasyl or hierarchical models systems like system r ims adabas total etc",
			 "information retrieval articles by gerard salton or others about clustering bibliographic coupling use of citations or co-citations the vector space model boolean search methods using inverted files feedback etc salton g",
			 "results relating parallel complexity theory both for prams and uniform circuits",
			 "algorithms for parallel computation and especially comparisons between parallel and sequential algorithms",
			 "list all articles on el1 and ecl el1 may be given as el1 i dont remember how they did it"}; 

	for (int k = 0;k <a.length;k++) {

	IndexReader reader = DirectoryReader.open(FSDirectory.open(new File(
		indexLocation)));
	IndexSearcher searcher = new IndexSearcher(reader);

	TopScoreDocCollector collector = TopScoreDocCollector.create(100, true);
	Query q=null;
	try {
		q = new QueryParser(Version.LUCENE_47, "contents",analyzer).parse(a[k]);
	
	searcher.search(q, collector);
	} catch (ParseException e) {
		// TODO Auto-generated catch block
		System.out.println("Exception");
		e.printStackTrace();
	}
	ScoreDoc[] hits = collector.topDocs().scoreDocs;

		// 4. display results
	File file = new File("D:\\g\\Task1\\Query_" + (k+1) + ".txt");
	file.createNewFile();
	BufferedWriter f=new BufferedWriter(new FileWriter(file));


		System.out.println("Query = " + a[k] + ", Found " + hits.length + " hits.");
		for (int i = 0; i < hits.length; ++i) {
		    int docId = hits[i].doc;
		    Document d = searcher.doc(docId);
		    
			
		    String stri = (k + 1) + " Q0 " + d.get("filename").split(".html")[0] + " " + (i+1)
			    + " " + hits[i].score + " Lucene";
		    f.write(stri);
			f.newLine();
		}
		
		f.flush();
		f.close();
	}
		// 5. term stats --> watch out for which "version" of the term
		// must be checked here instead!
	    

	}

    

    /**
     * Constructor
     * 
     * @param indexDir
     *            the name of the folder in which the index should be created
     * @throws java.io.IOException
     *             when exception creating index.
     */
    Task2(String indexDir) throws IOException {

	FSDirectory dir = FSDirectory.open(new File(indexDir));

	IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_47,
		analyzer);

	writer = new IndexWriter(dir, config);
    }

    /**
     * Indexes a file or directory
     * 
     * @param fileName
     *            the name of a text file or a folder we wish to add to the
     *            index
     * @throws java.io.IOException
     *             when exception
     */
    public void indexFileOrDirectory(String fileName) throws IOException {
	// ===================================================
	// gets the list of files in a folder (if user has submitted
	// the name of a folder) or gets a single file name (is user
	// has submitted only the file name)
	// ===================================================
	addFiles(new File(fileName));

	int originalNumDocs = writer.numDocs();
	for (File f : queue) {
	    FileReader fr = null;
	    try {
		Document doc = new Document();

		// ===================================================
		// add contents of file
		// ===================================================
		fr = new FileReader(f);
		doc.add(new TextField("contents", fr));
		doc.add(new StringField("path", f.getPath(), Field.Store.YES));
		doc.add(new StringField("filename", f.getName(),
			Field.Store.YES));

		writer.addDocument(doc);
		System.out.println("Added: " + f);
	    } catch (Exception e) {
		System.out.println("Could not add: " + f);
	    } finally {
		fr.close();
	    }
	}

	int newNumDocs = writer.numDocs();
	System.out.println("");
	System.out.println("****");
	System.out
		.println((newNumDocs - originalNumDocs) + " documents added.");
	System.out.println("****");

	queue.clear();
    }

    private void addFiles(File file) {

	if (!file.exists()) {
	    System.out.println(file + " does not exist.");
	}
	if (file.isDirectory()) {
	    for (File f : file.listFiles()) {
		addFiles(f);
	    }
	} else {
	    String filename = file.getName().toLowerCase();
	    // ===================================================
	    // Only index text files
	    // ===================================================
	    if (filename.endsWith(".htm") || filename.endsWith(".html")
		    || filename.endsWith(".xml") || filename.endsWith(".txt")) {
		queue.add(file);
	    } else {
		System.out.println("Skipped " + filename);
	    }
	}
    }

    /**
     * Close the index.
     * 
     * @throws java.io.IOException
     *             when exception closing
     */
    public void closeIndex() throws IOException {
	writer.close();
    }
}