package com.github.niqdev.todo

import com.github.niqdev.example.FreeB
import com.github.niqdev.example.and
import com.github.niqdev.example.or
import com.github.niqdev.example.pure

// TODO https://github.com/anchore/syft
// syft alpine -o syft-json=alpine-sbom.syft.json -o spdx-json=alpine-sbom.spdx.json -o cyclonedx-json=alpine-sbom.cyclonedx.json
// TODO https://falco.org/docs/rules/basic-elements
// TODO https://www.openpolicyagent.org/docs/latest/#rego

// grype modules/bool/src/test/resources/data/alpine-sbom.cyclonedx.json

data class Package(val name: String, val version: Int, val type: PackageType)

enum class PackageType {
  APK,
  DEBIAN,
  RPM,
  JAR,
  PYTHON
}

data class Sbom(
  val name: String,
  val packages: List<Package>
)

// https://osv.dev
// TODO semver
sealed interface VulnerabilityPredicate {
  data class IsName(val name: String) : VulnerabilityPredicate
  data class IsVersion(val version: Int) : VulnerabilityPredicate
  data class IsVersionLessThan(val version: Int) : VulnerabilityPredicate
  data class AnyVersionIn(val versions: List<Int>) : VulnerabilityPredicate
  data class IsPackage(val type: PackageType) : VulnerabilityPredicate

  companion object {
    fun eval(pkg: Package): (VulnerabilityPredicate) -> Boolean = {
      when (it) {
        is IsName -> it.name == pkg.name
        is IsVersion -> it.version == pkg.version
        is IsVersionLessThan -> pkg.version < it.version
        is AnyVersionIn -> it.versions.contains(pkg.version)
        is IsPackage -> it.type == pkg.type
      }
    }
  }
}

data class VulnerabilityCatalog(
  val vulnerabilities: FreeB<VulnerabilityPredicate>
)

// TODO extract CatalogPredicate?
fun VulnerabilityCatalog.eval(packages: List<Package>): (VulnerabilityPredicate) -> Boolean = {
  packages.fold(false) { isVulnerable, pkg ->
    isVulnerable || this.vulnerabilities.run(VulnerabilityPredicate.eval(pkg))
  }
}

fun main() {
  val sbom = Sbom(
    name = "alpine",
    packages = listOf(
      Package(name = "busybox", version = 3, type = PackageType.APK),
      Package(name = "foo", version = 5, type = PackageType.APK),
      Package(name = "bar", version = 8, type = PackageType.APK)
    )
  )

  val fooVulnerability: FreeB<VulnerabilityPredicate> =
    pure(VulnerabilityPredicate.IsName("foo")) and
      pure(VulnerabilityPredicate.AnyVersionIn(listOf(1, 5, 7))) and
      pure(VulnerabilityPredicate.IsPackage(PackageType.APK))

  val barVulnerability: FreeB<VulnerabilityPredicate> =
    pure(VulnerabilityPredicate.IsName("bar")) and
      pure(VulnerabilityPredicate.IsVersionLessThan(2)) and
      pure(VulnerabilityPredicate.IsPackage(PackageType.APK))

  val apkCatalog = VulnerabilityCatalog(
    vulnerabilities = fooVulnerability or barVulnerability
  )

  val isVulnerable = apkCatalog.vulnerabilities.run(apkCatalog.eval(sbom.packages))
  println(isVulnerable)
}
