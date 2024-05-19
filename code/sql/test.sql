SELECT provider.id, provider.agu_cui, provider.provider_type, provider.last_fetch
FROM provider
WHERE provider.provider_type = 'gas'
Order BY provider.id;