import argparse
import glob
import json
import os.path
import subprocess
import sys


def run_cmd(*args, **kwargs):

    print(*args)
    sys.stdout.flush()

    subprocess.check_call(*args, **kwargs, shell=True)


def extract_ant_zip(config):
    '''
    extracts apache ant zip file
    '''

    #-------------------------------------------------------------------------
    # was it already extracted?

    if any([os.path.isdir(x) for x in glob.glob("apache-ant*")]):
        print("apache-ant already extracted")

    else:
        cmd = '"{unzip_exe}" -q "{ant_tarball}"'.format(**config)
        run_cmd(cmd)

    ant_dir = None

    for d in glob.glob("apache-ant*"):

        if os.path.isdir(d):
            ant_dir = d
            break

    if not ant_dir:
        raise AssertionError("Failed to extract apache ant zip!")

    config['ant_exe'] = os.path.join(ant_dir, 'bin', 'ant')


def extract_jedit_tarball(config):
    '''
    extracts jedit tarball
    '''

    #-------------------------------------------------------------------------
    # was it already extracted?

    if any([os.path.isdir(x) for x in glob.glob("jEdit")]):
        print("jEdit already extracted")
        return

    #-------------------------------------------------------------------------
    # unzip

    cmd = '"{tar_exe}" xfj "{jedit_tarball}"'.format(**config)

    run_cmd(cmd)

    assert os.path.isdir('jEdit'), "Failed to extract jEdit tarball!"


def main():

    parser = argparse.ArgumentParser()

    parser.add_argument(
        '--config',
        type = str,
        default = 'config.json',
        help = 'The input config.json to process'
    )

    parser.add_argument(
        '--run',
        action = "store_true",
        default = False,
        help = 'Run the newly built jedit.jar'
    )

    parser.add_argument(
        '--test',
        action = "store_true",
        default = False,
        help = 'Run the test suite on jEdit'
    )

    args = parser.parse_args()

    #-------------------------------------------------------------------------
    # read config

    with open(args.config, 'r') as fd:
        config = json.load(fd)

    #-------------------------------------------------------------------------
    # unpack tools

    extract_ant_zip(config)
    extract_jedit_tarball(config)

    #-------------------------------------------------------------------------
    # patch ivysettings.xml

    cmd = 'cd jEdit && patch --forward < ..\ivysettings.xml.patch || echo ignoring exit code'

    run_cmd(cmd)

    #-------------------------------------------------------------------------
    # build jEdit

    # setup java_home

    os.environ['JAVA_HOME'] = config['jdk_dir']

    cmd = 'cd jEdit && "..\{ant_exe}"'.format(**config)

    run_cmd(cmd)

    assert os.path.isfile("jEdit/build/jedit.jar"), "Failed to build jedit.jar!"

    #-------------------------------------------------------------------------
    # test jEdit

    if args.test:

        cmd = 'cd jEdit && "..\{ant_exe}" test'.format(**config)

        run_cmd(cmd)

    #-------------------------------------------------------------------------
    # generate docs jEdit

    cmd = 'cd jEdit && "..\{ant_exe}" docs-html'.format(**config)

    run_cmd(cmd)

    #-------------------------------------------------------------------------
    # build OpenIt

    cmd = 'cd OpenIt && "..\{ant_exe}"'.format(**config)

    run_cmd(cmd)

    #-------------------------------------------------------------------------
    # build FinishHim

    cmd = 'cd FinishHim && "..\{ant_exe}"'.format(**config)

    run_cmd(cmd)

    #-------------------------------------------------------------------------
    # run jEdit

    cmd = 'cd jEdit && "..\{ant_exe}" run'.format(**config)

    run_cmd(cmd)



if __name__ == "__main__": main()
