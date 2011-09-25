package dk.nsi.stamdata.cpr;

import java.util.Set;

import com.google.common.base.Splitter;
import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

@Singleton
public class WhitelistProvider implements Provider<Set<String>>
{
	private final Set<String> entries;

	@Inject
	WhitelistProvider(@Named("whitelist") String whitelistProperty)
	{
		Iterable<String> values = Splitter.on(",").omitEmptyStrings().trimResults().split(whitelistProperty);
		entries = Sets.newHashSet(values);
	}
	
	@Override
	public Set<String> get()
	{
		return entries;
	}
}
