package cn.edu.zjnu.acm.judge.service.impl

import cn.edu.zjnu.acm.judge.domain.DomainLocale
import cn.edu.zjnu.acm.judge.mapper.LocaleMapper
import cn.edu.zjnu.acm.judge.service.LocaleService
import org.springframework.stereotype.Service
import java.util.*
import kotlin.Comparator

@Service("localeService")
class LocaleServiceImpl(
        @Suppress("SpringJavaInjectionPointsAutowiringInspection") private val localeMapper: LocaleMapper
) : LocaleService {

    override fun resolve(locale: Locale?): String? {
        val supported = toSupported(locale)
        return if (supported == Locale.ROOT) null else supported.toLanguageTag()
    }

    override fun toSupported(locale: Locale?): Locale {
        locale ?: return Locale.ROOT
        val candidateLocales = ControlHolder.CONTROL.getCandidateLocales("", locale)
        val collect = sortedSetOf(String.CASE_INSENSITIVE_ORDER, *findAll().map { it.id!! }.toTypedArray())
        for (candidateLocale in candidateLocales) {
            if (collect.contains(candidateLocale.toLanguageTag())) {
                return candidateLocale
            }
        }
        return Locale.ROOT
    }

    override fun toDomainLocale(locale: Locale, inLocale: Locale): DomainLocale {
        val domainLocale = localeMapper.findOne(locale.toLanguageTag())
        if (domainLocale != null) {
            return domainLocale
        }
        val displayName = locale.getDisplayName(inLocale)
        return DomainLocale(id = locale.toLanguageTag(), name = displayName)
    }

    override fun toDomainLocale(localeName: String, supportOnly: Boolean): DomainLocale {
        val locale = Locale.forLanguageTag(localeName)
        return toDomainLocale(if (supportOnly) toSupported(locale) else locale, locale)
    }

    override fun findAll(): List<DomainLocale> {
        return localeMapper.findAll()
    }

    override fun findOne(id: String): DomainLocale? {
        return if (Locale.ROOT.toLanguageTag().equals(id, ignoreCase = true)) null
        else localeMapper.findOne(id)
    }

    override fun support(all: Boolean): List<DomainLocale> {
        var locales = Locale.getAvailableLocales().asSequence()
        // if not all, only languages available
        if (!all) {
            locales = locales.map { locale -> Locale.forLanguageTag(locale.language) }
        }
        return locales.sortedWith(DEFAULT_LOCALE_COMPARATOR).distinct()
                .map { locale -> toDomainLocale(locale, locale) }
                .sortedWith(DEFAULT_DOMAIN_LOCALE_COMPARATOR).distinct().toList()
    }

    private object ControlHolder {
        val CONTROL: ResourceBundle.Control = ResourceBundle.Control.getNoFallbackControl(ResourceBundle.Control.FORMAT_PROPERTIES)
    }

    companion object {
        private val DEFAULT_LOCALE_COMPARATOR = Comparator.comparing<Locale, String> { it.toLanguageTag() }
        private val DEFAULT_DOMAIN_LOCALE_COMPARATOR = Comparator.comparing<DomainLocale, String> { it.name }
    }

}
