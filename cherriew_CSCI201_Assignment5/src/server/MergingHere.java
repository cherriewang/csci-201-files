package server;

import java.util.ArrayList;
import java.util.LinkedList;

public class MergingHere {

	public ArrayList<String> contentOfFiles = new ArrayList<String>();
	public String oldStuff = new String();
	
	public String fileNameJustInCase = new String();
	
	public String completedMerge(){
		diff_match_patch theThing = new diff_match_patch();
		LinkedList<diff_match_patch.Patch> patching = new LinkedList<diff_match_patch.Patch>();
		for(int i = 0 ; i < contentOfFiles.size(); i++){
			LinkedList<diff_match_patch.Patch> patchingAgain = theThing.patch_make(oldStuff, contentOfFiles.get(i));
			patching.addAll(patchingAgain);
		}
		
		contentOfFiles.clear();
		
		String weDone = new String();
		weDone = (String)(theThing.patch_apply(patching, oldStuff)[0]);
		oldStuff = weDone;
		return weDone;
	}
	
}
