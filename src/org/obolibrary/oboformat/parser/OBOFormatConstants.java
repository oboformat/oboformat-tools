package org.obolibrary.oboformat.parser;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

public class OBOFormatConstants {

	public enum OboFormatTag{
	
		TAG_FORMAT_VERSION("format-version"),
		TAG_ONTOLOGY( "ontology"),
		TAG_DATA_VERSION( "data-version"),
		TAG_DATE( "date"),
		TAG_SAVED_BY( "saved-by"),
		TAG_AUTO_GENERATED_BY( "auto-generated-by"),
		TAG_IMPORT( "import"),
		TAG_SUBSETDEF( "subsetdef"),
		TAG_SYNONYMTYPEDEF( "synonymtypedef"),
		TAG_DEFAULT_NAMESPACE( "default-namespace"),
		TAG_IDSPACE( "idspace"),
		TAG_TREAT_XREFS_AS_EQUIVALENT( "treat-xrefs-as-equivalent"),
		TAG_TREAT_XREFS_AS_GENUS_DIFFERENTIA( "treat-xrefs-as-genus-differentia"),
		TAG_TREAT_XREFS_AS_RELATIONSHIP( "treat-xrefs-as-relationship"),
		TAG_TREAT_XREFS_AS_IS_A( "treat-xrefs-as-is_a"),
		TAG_REMARK( "remark"),
		TAG_ID( "id"),
		TAG_NAME( "name"),
		TAG_NAMESPACE( "namespace"),
		TAG_ALT_ID( "alt_id"),
		TAG_DEF( "def"),
		TAG_COMMENT( "comment"),
		TAG_SUBSET( "subset"),
		TAG_SYNONYM( "synonym"),
		TAG_XREF( "xref"),
		TAG_BUILTIN( "builtin"),
		TAG_PROPERTY_VALUE( "property_value"),
		TAG_IS_A( "is_a"),
		TAG_INTERSECTION_OF( "intersection_of"),
		TAG_UNION_OF( "union_of"),
		TAG_EQUIVALENT_TO( "equivalent_to"),
		TAG_DISJOINT_FROM( "disjoint_from"),
		TAG_RELATIONSHIP( "relationship"),
		TAG_CREATED_BY( "created_by"),
		TAG_CREATION_DATE( "creation_date"),
		TAG_IS_OBSELETE( "is_obsolete"),
		TAG_REPLACED_BY( "replaced_by"),
		TAG_IS_ANONYMOUS( "is_anonymous"),
		TAG_DOMAIN( "domain"),
		TAG_RANGE( "range"),
		TAG_IS_ANTI_SYMMETRIC( "is_anti_symmetric"),
		TAG_IS_CYCLIC( "is_cyclic"),
		TAG_IS_REFLEXIVE( "is_reflexive"),
		TAG_IS_SYMMETRIC( "is_symmetric"),
		TAG_IS_TRANSITIVE( "is_transitive"),
		TAG_IS_FUNCTIONAL( "is_functional"),
		TAG_IS_INVERSE_FUNCTIONAL( "is_inverse_functional"),
		TAG_TRANSITIVE_OVER( "transitive_over"),
		TAG_HOLDS_OVER_CHAIN( "holds_over_chain"),
		TAG_EQUIVALENT_TO_CHAIN( "equivalent_to_chain"),
		TAG_DISJOINT_OVER( "disjoint_over"),
		TAG_EXPAND_ASSERTION_TO( "expand_assertion_to"),
		TAG_EXPAND_EXPRESSION_TO( "expand_expression_to"),
		TAG_IS_CLASS_LEVEL_TAG( "is_class_level_tag"),
		TAG_IS_METADATA_TAG("is_metadata_tag"),
		TAG_CONSIDER("consider"),
		TAG_INVERSE_OF("inverse_of"),
		TAG_IS_ASYMMETRIC("is_asymmetric");
		
		private String tag;
		
		OboFormatTag(String tag){
			this.tag = tag;
		}
	
		public String getTag(){
			return this.tag;
		}
		
		public String toString(){
			return this.tag;
		}
	}
	
	public final static Set<String> TAGS;
	
	private static Hashtable<String, OboFormatTag> tagsTable;
	
	
	static{
		 tagsTable = new Hashtable<String, OBOFormatConstants.OboFormatTag>();
		
		 for(OboFormatTag tag: OboFormatTag.values()){
			 tagsTable.put(tag.getTag(), tag);
		 }
		 
		 TAGS = tagsTable.keySet();
	}
	
	public static OboFormatTag getTag(String tag){
		return tagsTable.get(tag);
	}
	
}
