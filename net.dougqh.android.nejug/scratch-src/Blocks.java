import java.io.File;

import net.dougqh.blocks.Block;
import net.dougqh.blocks.BlocksGraph;
import net.dougqh.blocks.BlocksParser;
import net.dougqh.graphml.GraphmlWriter;

public class Blocks {
	public static final void main( final String... args ) throws Exception {
		BlocksGraph graph = BlocksParser.parseBlocks(
			new File( "scratch-src", "signum-block.dump" ),
			"method signum (I)I" );
		
		graph = graph.simplify();
		
		GraphmlWriter writer = new GraphmlWriter( new File( "output", "signum.graphml" ) ).forYed();
		try {
			writer.startGraphml();
			writer.startGraph();
			for ( Block block: graph.getBlocks() ) {
				writer.startNode( block.getId() );
				writer.yed().startShapeNode();
				writer.yed().nodeLabel( block.getCode() );
				writer.yed().endShapeNode();
				writer.endNode();
				
				for ( String nextId : block.getNextIds() ) {
					writer.directedEdge( block.getId(), nextId );
				}
			}
			writer.endGraph();
			writer.endGraphml();
		} finally {
			writer.close();
		}
	}
}
