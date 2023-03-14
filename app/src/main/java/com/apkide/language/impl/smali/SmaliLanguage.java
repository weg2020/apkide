package com.apkide.language.impl.smali;

import androidx.annotation.NonNull;

import com.apkide.language.api.CommonLanguage;
import com.apkide.language.api.Highlighter;

public class SmaliLanguage extends CommonLanguage {
	private SmaliHighlighter highlighter;
	
	@NonNull
	@Override
	public String getName() {
		return "Smali";
	}
	
	@NonNull
	@Override
	public Highlighter getHighlighter() {
		if (highlighter == null)
			highlighter = new SmaliHighlighter();
		return highlighter;
	}
	
	@NonNull
	@Override
	public String[] getDefaultFilePatterns() {
		return new String[]{"*.smali"};
	}
}
