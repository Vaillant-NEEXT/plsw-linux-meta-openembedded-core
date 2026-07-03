FILESEXTRAPATHS:prepend := "${THISDIR}/systemd:"

SUMMARY = "Systemctl executable from systemd"

require systemd.inc

DEPENDS = "gperf-native libcap-native util-linux-native python3-jinja2-native"

inherit pkgconfig meson native

MESON_TARGET = "systemctl"
MESON_INSTALL_TAGS = "systemctl"
# Explicitly disable features that meson auto-detects from the native sysroot.
# Only systemctl is built here; these prevent spurious dependencies and ensure
# reproducible builds regardless of what is installed on the build host.
EXTRA_OEMESON += "-Dlink-systemctl-shared=false"
EXTRA_OEMESON += "-Dpam=disabled -Daudit=disabled -Dselinux=disabled"
EXTRA_OEMESON += "-Dacl=disabled -Dapparmor=disabled -Dseccomp=disabled"
EXTRA_OEMESON += "-Dlibcryptsetup=disabled -Dlibcurl=disabled -Dlibfido2=disabled"
EXTRA_OEMESON += "-Dpcre2=disabled -Dp11kit=disabled -Dopenssl=disabled"

# Systemctl is supposed to operate on target, but the target sysroot is not
# determined at run-time, but rather set during configure
# More details are here https://github.com/systemd/systemd/issues/35897#issuecomment-2665405887
EXTRA_OEMESON += "--sysconfdir ${sysconfdir_native}"
