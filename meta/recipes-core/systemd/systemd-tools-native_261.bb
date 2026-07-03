# SPDX-License-Identifier: MIT
FILESEXTRAPATHS:prepend := "${THISDIR}/systemd:"

SUMMARY = "systemd native tools (systemctl and systemd-hwdb)"

require systemd.inc

DEPENDS = "gperf-native libcap-native util-linux-native python3-jinja2-native"

# TODO: Remove STATX_MNT_ID patch once minimum supported build host kernel is >= 5.8 (RHEL 8 EOL: 2029)
SRC_URI += "file://Handle-missing-pidfd_open-and-STATX_MNT_ID-on-older-.patch \
            file://hwdb-use-compat-mode-for-reproducible-cross-builds.patch \
           "

inherit pkgconfig meson native

# Build both tools from a single configured tree.
MESON_TARGET = "systemctl systemd-hwdb"

# Target-absolute paths that satisfy both tools from one meson configure:
#  - systemd-hwdb needs prefix=/usr so the compiled-in UDEVLIBEXECDIR
#    (/usr/lib/udev) matches the target rootfs layout, letting
#    "update --root $D --usr" find hwdb.d sources and write hwdb.bin there.
#  - systemctl needs sysconfdir=/etc; it operates on the target rootfs but the
#    sysroot is fixed at configure time rather than run time.
#    See https://github.com/systemd/systemd/issues/35897#issuecomment-2665405887
EXTRA_OEMESON += "--prefix /usr --sysconfdir /etc"
EXTRA_OEMESON += "-Dhwdb=true -Dlink-udev-shared=false -Dlink-systemctl-shared=false"

# Explicitly disable features that meson auto-detects from the native sysroot.
# These prevent spurious dependencies and ensure reproducible builds regardless
# of what is installed on the build host.
EXTRA_OEMESON += "-Dpam=disabled -Daudit=disabled -Dselinux=disabled"
EXTRA_OEMESON += "-Dacl=disabled -Dapparmor=disabled -Dseccomp=disabled"
EXTRA_OEMESON += "-Dlibcryptsetup=disabled -Dlibcurl=disabled -Dlibfido2=disabled"
EXTRA_OEMESON += "-Dpcre2=disabled -Dp11kit=disabled -Dopenssl=disabled"

do_install() {
    install -d ${D}${bindir}
    install -m 0755 ${B}/systemctl ${D}${bindir}/systemctl
    install -m 0755 ${B}/systemd-hwdb ${D}${bindir}/systemd-hwdb
}
